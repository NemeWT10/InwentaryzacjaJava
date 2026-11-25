package pl.pollub.inwentaryzacja.client.model;

public record User(Long id, String login, Role role) {

    @Override
    public String toString() {
        return login + " (" + role + ")";
    }
}
