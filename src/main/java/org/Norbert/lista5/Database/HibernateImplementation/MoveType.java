package org.Norbert.lista5.Database.HibernateImplementation;

import javax.persistence.Embeddable;
import java.io.Serializable;

public enum MoveType implements Serializable {
    SKIP,
    SURRENDER,
    CHECKER_MOVE
}
