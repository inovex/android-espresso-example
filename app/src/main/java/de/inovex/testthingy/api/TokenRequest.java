package de.inovex.testthingy.api;

/**
 * API Token Request
 */
public class TokenRequest {
    public String email;
    public String password;

    public TokenRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
