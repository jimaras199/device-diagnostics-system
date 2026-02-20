namespace DeviceDiagnostics.Api.Domain;

public class Device
{
    public int Id { get; set; }
    public int OwnerUserId { get; set; }
    public string Name { get; set; } = "";
    public string? Model { get; set; }
    public DateTime LastSeen { get; set; } = DateTime.UtcNow;
}
