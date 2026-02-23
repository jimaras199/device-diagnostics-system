using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using DeviceDiagnostics.Api.Contracts;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Authorization;
using DeviceDiagnostics.Api.Contracts.Responses;

namespace DeviceDiagnostics.Api.Controllers;

[Authorize]
[ApiController]
[Route("devices")]
public class DevicesController : ControllerBase
{
    private readonly AppDbContext _db;

    public DevicesController(AppDbContext db) => _db = db;


    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<DeviceResponse>))]
    public async Task<ActionResult<List<DeviceResponse>>> GetDevices(CancellationToken ct)
    {
        var userId = User.GetUserId();

        var devices = await _db.Devices
            .AsNoTracking()
            .Where(d => d.OwnerUserId == userId)
            .OrderByDescending(d => d.LastSeen)
            .Select(d => d.ToResponse())
            .ToListAsync(ct);

        return Ok(devices);
    }

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(DeviceResponse))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    public async Task<ActionResult<DeviceResponse>> CreateDevice([FromBody] CreateDeviceRequest request, CancellationToken ct)
    {
        var userId = User.GetUserId();

        var device = new Device
        {
            OwnerUserId = userId,
            Name = request.Name.Trim(),
            Model = string.IsNullOrWhiteSpace(request.Model) ? null : request.Model.Trim(),
            LastSeen = DateTime.UtcNow
        };

        _db.Devices.Add(device);
        await _db.SaveChangesAsync(ct);

        var response = device.ToResponse();
        return Created($"/devices/{device.Id}", response);
    }

    [HttpGet("{id:int}")]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(DeviceResponse))]
    public async Task<ActionResult<DeviceResponse>> GetDeviceById(int id, CancellationToken ct)
    {
        var userId = User.GetUserId();

        var device = await _db.Devices
            .AsNoTracking()
            .Where(d => d.Id == id && d.OwnerUserId == userId)
            .Select(d => d.ToResponse())
            .FirstOrDefaultAsync(ct);

        if (device is null)
            return NotFound(ApiErrors.NotFound($"Device {id} was not found."));

        return Ok(device);
    }
}