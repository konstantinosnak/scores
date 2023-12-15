package gr.aueb.cf.scoresapp.service.exceptions;

public class EntityNotFoundException extends Exception{
    private static final long serialVersionUID = 12345678L;

    public EntityNotFoundException(Class<?> entityClass, Long id) {
        super("Entity " + entityClass.getSimpleName() + " with id " + id + " does not exist");
    }
}