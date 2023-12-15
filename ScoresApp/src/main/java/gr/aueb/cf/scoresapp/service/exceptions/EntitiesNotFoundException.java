package gr.aueb.cf.scoresapp.service.exceptions;

public class EntitiesNotFoundException extends Exception{
    private static final long serialVersionUID = 12345678L;

    public EntitiesNotFoundException(Class<?> entityClass) {
        super("Entities " + entityClass.getSimpleName() + " do not exist");
    }
}
