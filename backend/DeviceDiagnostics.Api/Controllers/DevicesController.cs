using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using DeviceDiagnostics.Api.Contracts;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Authorization;
using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Services;

namespace DeviceDiagnostics.Api.Controllers;

[Authorize]
[ApiController]
[Route("devices")]
public class DevicesController : ControllerBase
{
    private readonly DevicesService _devicesService;

    public DevicesController(DevicesService devicesService)
    {
        _devicesService = devicesService;
    }


    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<DeviceResponse>))]
    public async Task<ActionResult<List<DeviceResponse>>> GetDevices(CancellationToken ct)
    {
        var userId = User.GetUserId();

        var devices = await _devicesService.GetDevicesAsync(userId, ct);

        return Ok(devices);
    }

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(DeviceResponse))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    public async Task<ActionResult<DeviceResponse>> CreateDevice([FromBody] CreateDeviceRequest request, CancellationToken ct)
    {
        var userId = User.GetUserId();

        var response = await _devicesService.CreateDeviceAsync(userId, request, ct);

        return Created($"/devices/{response.Id}", response);
    }

    [HttpGet("{id:int}")]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(DeviceResponse))]
    public async Task<ActionResult<DeviceResponse>> GetDeviceById(int id, CancellationToken ct)
    {
        var userId = User.GetUserId();

        var device = await _devicesService.GetDeviceByIdAsync(userId, id, ct);

        if (device is null)
            return NotFound(ApiErrors.NotFound($"Device {id} was not found."));

        return Ok(device);
    }
}