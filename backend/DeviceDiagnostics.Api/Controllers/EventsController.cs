using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using DeviceDiagnostics.Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[ApiController]
[Authorize]
[Route("devices/{deviceId:int}/events")]
public class EventsController : ControllerBase
{
    private readonly EventsService _eventsService;

    public EventsController(EventsService eventsService)
    {
        _eventsService = eventsService;
    }

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(EventResponse))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<EventResponse>> Create(int deviceId,[FromBody] CreateEventRequest request,CancellationToken ct)
    {
        var userId = User.GetUserId();

        var deviceOwned = await _eventsService.DeviceBelongsToUserAsync(
            userId,
            deviceId,
            ct);

        if (!deviceOwned)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var response = await _eventsService.CreateEventAsync(
            userId,
            deviceId,
            request,
            ct);

        return Created($"/devices/{deviceId}/events/{response.Id}", response);
    }

    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<EventResponse>))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<List<EventResponse>>> Get(int deviceId,[FromQuery] DateTime? fromUtc,[FromQuery] DateTime? toUtc,[FromQuery] string? level,CancellationToken ct)
    {
        var userId = User.GetUserId();

        var deviceOwned = await _eventsService.DeviceBelongsToUserAsync(
            userId,
            deviceId,
            ct);

        if (!deviceOwned)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var items = await _eventsService.GetEventsAsync(
            userId,
            deviceId,
            ct);

        return Ok(items);
    }
}
