/**
 * touch.js — translates mobile touch gestures into mouse/wheel events that
 * CheerpJ forwards into JME3's InputManager.
 *
 * Gesture map:
 *   1-finger tap         → left-click  (select unit / deselect)
 *   1-finger drag        → middle-mouse drag (camera pan)
 *   2-finger tap         → right-click (move-to / attack command)
 *   2-finger pinch       → mouse wheel (zoom in/out)
 *   long press (600ms)   → right-click (alternative for move command)
 */
(function () {
  'use strict';

  const LONG_PRESS_MS  = 600;
  const TAP_MAX_MOVE   = 10;   // pixels — movement above this cancels a tap
  const PINCH_SCALE    = 0.04; // wheel delta multiplier per pixel of pinch change

  let canvas = null;

  // Wait until CheerpJ creates the game canvas before attaching listeners.
  function attachWhenReady() {
    canvas = document.getElementById('canvas');
    if (!canvas || !('ontouchstart' in window)) return;
    canvas.addEventListener('touchstart',  onTouchStart,  { passive: false });
    canvas.addEventListener('touchmove',   onTouchMove,   { passive: false });
    canvas.addEventListener('touchend',    onTouchEnd,    { passive: false });
    canvas.addEventListener('touchcancel', onTouchCancel, { passive: false });
  }

  // ── State ────────────────────────────────────────────────────────────────

  let activeTouches   = {};   // pointerId → {x, y, startX, startY, startTime}
  let longPressTimer  = null;
  let lastPinchDist   = null;
  let isPanning       = false;
  let isPinching      = false;

  function touchCount() { return Object.keys(activeTouches).length; }

  function pinchDistance() {
    const pts = Object.values(activeTouches);
    if (pts.length < 2) return null;
    const dx = pts[0].x - pts[1].x;
    const dy = pts[0].y - pts[1].y;
    return Math.hypot(dx, dy);
  }

  function midpoint() {
    const pts = Object.values(activeTouches);
    return {
      x: (pts[0].x + pts[1].x) / 2,
      y: (pts[0].y + pts[1].y) / 2,
    };
  }

  // ── Synthetic event helpers ──────────────────────────────────────────────

  function clientToCanvas(x, y) {
    const rect = canvas.getBoundingClientRect();
    return {
      x: ((x - rect.left) / rect.width)  * canvas.width,
      y: ((y - rect.top)  / rect.height) * canvas.height,
    };
  }

  function fireMouseEvent(type, clientX, clientY, button) {
    const pos = clientToCanvas(clientX, clientY);
    canvas.dispatchEvent(new MouseEvent(type, {
      bubbles: true, cancelable: true,
      clientX: clientX, clientY: clientY,
      screenX: pos.x,  screenY: pos.y,
      button:  button  || 0,
      buttons: button === 2 ? 2 : (type === 'mousedown' || type === 'mousemove' ? 1 : 0),
    }));
  }

  function fireWheelEvent(clientX, clientY, deltaY) {
    canvas.dispatchEvent(new WheelEvent('wheel', {
      bubbles: true, cancelable: true,
      clientX: clientX, clientY: clientY,
      deltaY:  deltaY,
      deltaMode: WheelEvent.DOM_DELTA_PIXEL,
    }));
  }

  function click(clientX, clientY, button) {
    fireMouseEvent('mousedown', clientX, clientY, button);
    fireMouseEvent('mouseup',   clientX, clientY, button);
    if (button !== 2) {
      canvas.dispatchEvent(new MouseEvent('click', {
        bubbles: true, cancelable: true,
        clientX: clientX, clientY: clientY,
        button: 0, buttons: 0,
      }));
    } else {
      canvas.dispatchEvent(new MouseEvent('contextmenu', {
        bubbles: true, cancelable: true,
        clientX: clientX, clientY: clientY,
        button: 2, buttons: 0,
      }));
    }
  }

  // ── Gesture handlers ────────────────────────────────────────────────────

  function cancelLongPress() {
    if (longPressTimer) { clearTimeout(longPressTimer); longPressTimer = null; }
  }

  function onTouchStart(e) {
    e.preventDefault();
    Array.from(e.changedTouches).forEach(t => {
      activeTouches[t.identifier] = {
        x: t.clientX, y: t.clientY,
        startX: t.clientX, startY: t.clientY,
        startTime: Date.now(),
      };
    });

    isPinching = false;
    cancelLongPress();

    if (touchCount() === 1) {
      const t = Object.values(activeTouches)[0];
      // Start long-press timer for right-click alternative.
      longPressTimer = setTimeout(() => {
        longPressTimer = null;
        isPanning = false;
        click(t.x, t.y, 2);
      }, LONG_PRESS_MS);

      // Start panning state (will be confirmed on move).
      isPanning = false;
      fireMouseEvent('mousemove', t.x, t.y, 0);

    } else if (touchCount() === 2) {
      cancelLongPress();
      isPanning = false;
      lastPinchDist = pinchDistance();
    }
  }

  function onTouchMove(e) {
    e.preventDefault();
    Array.from(e.changedTouches).forEach(t => {
      if (activeTouches[t.identifier]) {
        activeTouches[t.identifier].x = t.clientX;
        activeTouches[t.identifier].y = t.clientY;
      }
    });

    if (touchCount() === 1) {
      const t = Object.values(activeTouches)[0];
      const dx = t.x - t.startX;
      const dy = t.y - t.startY;
      if (!isPanning && Math.hypot(dx, dy) > TAP_MAX_MOVE) {
        cancelLongPress();
        isPanning = true;
        // Begin middle-mouse-button pan.
        fireMouseEvent('mousedown', t.x, t.y, 1);
      }
      if (isPanning) {
        fireMouseEvent('mousemove', t.x, t.y, 1);
      }

    } else if (touchCount() >= 2) {
      cancelLongPress();
      isPinching = true;
      const dist = pinchDistance();
      if (lastPinchDist !== null) {
        const delta = (lastPinchDist - dist) * PINCH_SCALE * 100;
        const mp = midpoint();
        fireWheelEvent(mp.x, mp.y, delta);
      }
      lastPinchDist = dist;
    }
  }

  function onTouchEnd(e) {
    e.preventDefault();
    const released = Array.from(e.changedTouches);

    released.forEach(t => {
      const stored = activeTouches[t.identifier];
      if (!stored) return;

      const elapsed = Date.now() - stored.startTime;
      const dx = t.clientX - stored.startX;
      const dy = t.clientY - stored.startY;
      const moved = Math.hypot(dx, dy);

      if (touchCount() === 1 && !isPanning && !isPinching && moved < TAP_MAX_MOVE && elapsed < LONG_PRESS_MS) {
        cancelLongPress();
        // Single tap → left-click.
        click(t.clientX, t.clientY, 0);
      }

      delete activeTouches[t.identifier];
    });

    // Two-finger tap (both fingers lifted quickly, no pinch movement).
    if (touchCount() === 0 && !isPinching) {
      if (released.length >= 2) {
        const t = released[0];
        const elapsed = Date.now() - (activeTouches[t.identifier]?.startTime || 0);
        if (elapsed < LONG_PRESS_MS) {
          cancelLongPress();
          click(t.clientX, t.clientY, 2);
        }
      }
    }

    if (touchCount() === 0) {
      if (isPanning) {
        const t = released[0];
        fireMouseEvent('mouseup', t.clientX, t.clientY, 1);
      }
      isPanning    = false;
      isPinching   = false;
      lastPinchDist = null;
      cancelLongPress();
    } else if (touchCount() === 1) {
      isPinching    = false;
      lastPinchDist = null;
    }
  }

  function onTouchCancel(e) {
    e.preventDefault();
    Array.from(e.changedTouches).forEach(t => delete activeTouches[t.identifier]);
    if (isPanning) fireMouseEvent('mouseup', 0, 0, 1);
    isPanning    = false;
    isPinching   = false;
    lastPinchDist = null;
    cancelLongPress();
  }

  // Attach once DOM is ready.
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', attachWhenReady);
  } else {
    attachWhenReady();
  }
})();
