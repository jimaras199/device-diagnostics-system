using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using DeviceDiagnostics.Api.Contracts;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[ApiController]
[Route("devices")]
public class DevicesController : ControllerBase
{
    private readonly AppDbContext _db;

    public DevicesController(AppDbContext db) => _db = db;
    

    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(Device))]
    public async Task<ActionResult<List<Device>>> GetDevices(CancellationToken ct)
    {
        var devices = await _db.Devices
            .OrderByDescending(d => d.LastSeen)
            .ToListAsync(ct);

        return Ok(devices);
    }

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(Device))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    public async Task<ActionResult<Device>> CreateDevice([FromBody] CreateDeviceRequest request, CancellationToken ct)
    {
        var device = new Device
        {
            Name = request.Name.Trim(),
            Model = string.IsNullOrWhiteSpace(request.Model) ? null : request.Model.Trim(),
            LastSeen = DateTime.UtcNow
        };

        _db.Devices.Add(device);
        await _db.SaveChangesAsync(ct);

        return Created($"/devices/{device.Id}", device);
    }

    [HttpGet("{id:int}")]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(Device))]
    public async Task<ActionResult<Device>> GetDeviceById(int id, CancellationToken ct)
    {
        var device = await _db.Devices.FirstOrDefaultAsync(d => d.Id == id, ct);
        if (device is null)
            return NotFound(ApiErrors.NotFound($"Device {id} was not found."));

        return Ok(device);
    }
}