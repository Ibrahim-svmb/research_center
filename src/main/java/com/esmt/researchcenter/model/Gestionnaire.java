package com.esmt.researchcenter.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@PrimaryKeyJoinColumn(name = "id_user")
public class Gestionnaire extends User implements Serializable {

    public Gestionnaire() {
    }

    public Gestionnaire(Long id, String username, String password, String lastName, String firstName, TypeUser role, String provider, String providerId) {
        super(id, username, password, lastName, firstName, role, provider, providerId);
    }

    @Override
    public String toString() {
        return "Gestionnaire{" +
                "username='" + getUsername() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                '}';
    }
}
