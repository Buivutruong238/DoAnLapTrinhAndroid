package com.nhom08.doanlaptrinhandroid.DTO;

public class Wp_term {
    private int term_id;
    private String name;

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

    public Wp_term(){}

    public Wp_term(int id, String tittle) {
        this.term_id = id;
        this.name = tittle;
    }

    @Override
    public String toString() {
        return "Wp_term_DAL{" +
                "term_id=" + term_id +
                ", name='" + name + '\'' +
                '}';
    }
}