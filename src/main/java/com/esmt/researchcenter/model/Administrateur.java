package com.esmt.researchcenter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Entity
@PrimaryKeyJoinColumn(name = "id_user")
@Getter
@Setter
public class Administrateur extends User implements Serializable {

    public Administrateur() {
    }

    public Administrateur(Long id, String username, String password, String lastName, String firstName, TypeUser role, String provider, String providerId) {
        super(id, username, password, lastName, firstName, role, provider, providerId);
    }

    @Override
    public String toString() {
        return "Administrateur{" +
                "username='" + getUsername() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                '}';
    }
}
