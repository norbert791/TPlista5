package org.Norbert.lista5.Database.HibernateImplementation;

import javax.persistence.*;

@Entity
@Table(name = "CheckerMove")
public class CheckerMove {
    @Id
    @GeneratedValue
    int id;
    int oldX;
    int oldY;
    int newX;
    int newY;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOldX() {
        return oldX;
    }

    public void setOldX(int oldX) {
        this.oldX = oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public void setOldY(int oldY) {
        this.oldY = oldY;
    }

    public int getNewX() {
        return newX;
    }

    public void setNewX(int newX) {
        this.newX = newX;
    }

    public int getNewY() {
        return newY;
    }

    public void setNewY(int newY) {
        this.newY = newY;
    }
}
