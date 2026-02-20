using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace DeviceDiagnostics.Api.Migrations
{
    /// <inheritdoc />
    public partial class AddDeviceOwner : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "OwnerUserId",
                table: "Devices",
                type: "INTEGER",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.CreateIndex(
                name: "IX_Devices_OwnerUserId_LastSeen",
                table: "Devices",
                columns: new[] { "OwnerUserId", "LastSeen" });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "IX_Devices_OwnerUserId_LastSeen",
                table: "Devices");

            migrationBuilder.DropColumn(
                name: "OwnerUserId",
                table: "Devices");
        }
    }
}
