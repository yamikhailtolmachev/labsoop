package dto.request;
import java.util.UUID;

public class AddOperationRequest {
    private UUID function1Id;
    private UUID function2Id;

    public UUID getFunction1Id() { return function1Id; }
    public void setFunction1Id(UUID function1Id) { this.function1Id = function1Id; }
    public UUID getFunction2Id() { return function2Id; }
    public void setFunction2Id(UUID function2Id) { this.function2Id = function2Id; }
}