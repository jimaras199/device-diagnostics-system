namespace DeviceDiagnostics.Api.Contracts.Responses;

public class EventResponse
{
    public int Id { get; set; }
    public string Level { get; set; } = "";
    public string Message { get; set; } = "";
    public DateTime TimestampUtc { get; set; }
}
