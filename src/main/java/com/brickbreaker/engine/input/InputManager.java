package com.brickbreaker.engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputManager implements KeyListener {
    private static final Logger logger = LogManager.getLogger(InputManager.class);
    private final Map<Integer, Command> keyBindings = new HashMap<>();

    public void bindKey(int keyCode, Command command) {
        keyBindings.put(keyCode, command);
        logger.debug("Bound keycode {} to command", keyCode);
    }

    public void unbindKey(int keyCode) {
        keyBindings.remove(keyCode);
        logger.debug("Unbound keycode {}", keyCode);
    }

    public void clearBindings() {
        keyBindings.clear();
        logger.debug("Cleared all key bindings.");
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No-op
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Command command = keyBindings.get(e.getKeyCode());
        if (command != null) {
            command.execute();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No-op
    }
}
