namespace DeviceDiagnostics.Api.Contracts.Responses;

public class DeviceResponse
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public string? Model { get; set; }
    public DateTime LastSeenUtc { get; set; }
}