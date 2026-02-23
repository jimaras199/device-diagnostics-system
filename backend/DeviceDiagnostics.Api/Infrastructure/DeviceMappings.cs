using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;

namespace DeviceDiagnostics.Api.Infrastructure;

public static class DeviceMappings
{
    public static DeviceResponse ToResponse(this Device d) => new()
    {
        Id = d.Id,
        Name = d.Name,
        Model = d.Model,
        LastSeen = d.LastSeen
    };
}