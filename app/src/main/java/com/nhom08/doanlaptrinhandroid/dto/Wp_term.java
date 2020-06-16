package com.nhom08.doanlaptrinhandroid.dto;

public class Wp_term {
    private int term_id;
    private String name;

    public Wp_term() {
    }

    public Wp_term(int term_id, String name) {
        this.term_id = term_id;
        this.name = name;
    }

    public int getTerm_id() {
        return term_id;
    }

    public void setTerm_id(int term_id) {
        this.term_id = term_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
