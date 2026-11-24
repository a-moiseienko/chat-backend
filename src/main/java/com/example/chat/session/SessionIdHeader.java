package com.example.chat.session;

/** Header the widget sends the per-tab session id in (see the widget's session-per-tab ADR). */
public final class SessionIdHeader {

    public static final String NAME = "X-Session-Id";

    private SessionIdHeader() {
    }
}
