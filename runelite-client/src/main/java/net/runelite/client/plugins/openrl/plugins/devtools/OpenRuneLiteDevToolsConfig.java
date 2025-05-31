package net.runelite.client.plugins.openrl.plugins.devtools;

import java.awt.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(OpenRuneLiteDevToolsConfig.GROUP)
public interface OpenRuneLiteDevToolsConfig extends Config
{
	String GROUP = "openrldevtools";

	@ConfigSection(
		name = "Settings",
		description = "",
		position = 0,
		closedByDefault = true
	)
	String displayedInfo = "Settings";

	@ConfigItem(
		keyName = "ids",
		name = "IDs",
		description = "Show ids",
		position = 1,
		section = displayedInfo
	)
	default boolean ids()
	{
		return true;
	}

	@ConfigItem(
		keyName = "names",
		name = "Names",
		description = "Show names",
		position = 2,
		section = displayedInfo
	)
	default boolean names()
	{
		return true;
	}

	@ConfigItem(
		keyName = "actions",
		name = "Actions",
		description = "Show actions",
		section = displayedInfo,
		position = 3
	)
	default boolean actions()
	{
		return true;
	}

	@ConfigItem(
		keyName = "locations",
		name = "World locations",
		description = "Show world locations",
		position = 4,
		section = displayedInfo
	)
	default boolean worldLocations()
	{
		return true;
	}

	@ConfigItem(
		keyName = "indexes",
		name = "Indexes (Actors)",
		description = "Show indexes",
		position = 5,
		section = displayedInfo
	)
	default boolean indexes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "animations",
		name = "Animations",
		description = "Show animations",
		position = 6,
		section = displayedInfo
	)
	default boolean animations()
	{
		return true;
	}

	@ConfigItem(
		keyName = "graphics",
		name = "Graphic (Actors)",
		description = "Show graphics",
		position = 7,
		section = displayedInfo
	)
	default boolean graphics()
	{
		return true;
	}

	@ConfigItem(
		keyName = "quantities",
		name = "Quantities (TileItems)",
		description = "Show quantities",
		position = 8,
		section = displayedInfo
	)
	default boolean quantities()
	{
		return true;
	}

	@ConfigItem(
		keyName = "trueWorldLocations",
		name = "True world locations",
		description = "Show true world locations in instances",
		position = 9,
		section = displayedInfo
	)
	default boolean trueWorldLocations()
	{
		return false;
	}

	@ConfigSection(
		name = "Tile Objects",
		description = "",
		position = 1,
		closedByDefault = true
	)
	String tileObjects = "Tile Objects";

	@ConfigItem(
		keyName = "gameObjects",
		name = "Game Objects",
		description = "Render Game Objects",
		section = tileObjects
	)
	default boolean gameObjects()
	{
		return false;
	}

	@ConfigItem(
		keyName = "decorObjects",
		name = "Decorative Objects",
		description = "Render Decorative Objects",
		section = tileObjects
	)
	default boolean decorObjects()
	{
		return false;
	}

	@ConfigItem(
		keyName = "wallObjects",
		name = "Wall Objects",
		description = "Render Wall Objects",
		section = tileObjects
	)
	default boolean wallObjects()
	{
		return false;
	}

	@ConfigItem(
		keyName = "groundObjects",
		name = "Ground Objects",
		description = "Render Ground Objects",
		section = tileObjects
	)
	default boolean groundObjects()
	{
		return false;
	}

	@ConfigItem(
		keyName = "tileItems",
		name = "TileItems",
		description = "Render Tile Items",
		section = tileObjects
	)
	default boolean tileItems()
	{
		return false;
	}

	@ConfigSection(
		name = "Actors",
		description = "",
		position = 2,
		closedByDefault = true
	)
	String actors = "Actors";

	@ConfigItem(
		keyName = "npcs",
		name = "NPCs",
		description = "Render NPCs",
		section = actors
	)
	default boolean npcs()
	{
		return false;
	}

	@ConfigItem(
		keyName = "players",
		name = "Players",
		description = "Render Players",
		section = actors
	)
	default boolean players()
	{
		return false;
	}

	@ConfigSection(
		name = "Others",
		description = "",
		position = 9,
		closedByDefault = true
	)
	String others = "Others";

	@ConfigItem(
		keyName = "projectiles",
		name = "Projectiles",
		description = "Render Projectiles",
		section = others
	)
	default boolean projectiles()
	{
		return false;
	}

	@ConfigItem(
		keyName = "graphicsObjects",
		name = "Graphics Objects",
		description = "Render Graphics Objects",
		section = others
	)
	default boolean graphicsObjects()
	{
		return false;
	}

	@ConfigItem(
		keyName = "inventory",
		name = "Inventory",
		description = "Render Inventory",
		section = others
	)
	default boolean inventory()
	{
		return false;
	}

	@ConfigItem(
		keyName = "tileLocation",
		name = "Tile Location",
		description = "Render Tile Location",
		section = others
	)
	default boolean tileLocation()
	{
		return false;
	}

	@ConfigItem(
		keyName = "path",
		name = "Last path",
		description = "Render calculated Path",
		position = 200,
		section = others
	)
	default boolean path()
	{
		return false;
	}

	@Range(max = 2)
	@ConfigItem(
		keyName = "staffLevel",
		name = "Staff level",
		description = "Used for jmod debugging tools",
		position = 201,
		section = others
	)
	default int staffLevel()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "memoryUsage",
		name = "Memory usage",
		description = "Render memory usage",
		position = 202,
		section = others
	)
	default boolean memoryUsage()
	{
		return false;
	}

	@ConfigSection(
		name = "Packet debugger",
		description = "",
		position = 10,
		closedByDefault = true
	)
	String packets = "Packets";

	@ConfigItem(
		keyName = "packets",
		name = "Client Packets",
		description = "Packets",
		position = 203,
		section = packets
	)
	default boolean packets()
	{
		return false;
	}

	@ConfigItem(
		keyName = "serverPackets",
		name = "Server Packets",
		description = "Server Packets",
		position = 204,
		section = packets
	)
	default boolean serverPackets()
	{
		return false;
	}

	@ConfigItem(
		keyName = "opcodes",
		name = "Opcodes",
		description = "Opcodes to log",
		position = 205,
		section = packets
	)
	default String opcodes()
	{
		return "";
	}

	@ConfigItem(
		keyName = "hexDump",
		name = "Hex dump",
		description = "Create a hex dump of the payload",
		position = 206,
		section = packets
	)
	default boolean hexDump()
	{
		return false;
	}

	@ConfigSection(
		name = "Regions",
		description = "",
		position = 4,
		closedByDefault = true
	)
	String regions = "regions";

	@Range(max = 3)
	@ConfigItem(
		keyName = "collisionOverlayPlane",
		name = "Collision overlay plane",
		description = "Collision overlay plane",
		position = 1,
		section = regions
	)
	default int collisionOverlayPlane()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "collisionOverlay",
		name = "Show collision overlay",
		description = "Show collision overlay",
		position = 2,
		section = regions
	)
	default boolean collisionOverlay()
	{
		return false;
	}

	@ConfigItem(
		keyName = "transportsOverlay",
		name = "Show transports overlay",
		description = "Show transports overlay",
		position = 3,
		section = regions
	)
	default boolean transportsOverlay()
	{
		return false;
	}

	@ConfigItem(
		keyName = "pathOverlay",
		name = "Show path overlay",
		description = "Show path overlay",
		position = 4,
		section = regions
	)
	default boolean pathOverlay()
	{
		return false;
	}

	@ConfigItem(
		keyName = "transportCreator",
		name = "Transport Creator",
		description = "Add debug menu to make transports",
		position = 5,
		section = regions
	)
	default boolean transportCreator()
	{
		return false;
	}

	@ConfigItem(
		keyName = "useCreatedTransports",
		name = "Use created transports",
		description = "add created transports to transport list",
		position = 6,
		section = regions
	)
	default boolean useCreatedTransports()
	{
		return false;
	}

	@ConfigItem(
		keyName = "pathDebugLocation",
		name = "Path debug location",
		description = "Location to debug pathing. Format: x y z",
		position = 7,
		section = regions
	)
	default String pathDebugLocation()
	{
		return "";
	}

	@ConfigItem(
		keyName = "pathDebugButton",
		name = "Debug pathing",
		description = "Debug pathing to above location",
		position = 8,
		section = regions

	)
	default Button pathDebugButton()
	{
		return new Button();
	}

	@ConfigSection(
		name = "Interaction",
		description = "",
		position = 5,
		closedByDefault = true
	)
	String interaction = "interaction";

	@ConfigItem(
		keyName = "drawMouse",
		name = "Draw mouse events",
		description = "Draws the sent mouse events on screen",
		section = interaction,
		position = 7
	)
	default boolean drawMouse()
	{
		return false;
	}

	@ConfigItem(
		keyName = "mousePositionOverlay",
		name = "Draw mouse position",
		description = "Draw the current mouse position and hex color of the hovered pixels",
		section = interaction,
		position = 8
	)
	default boolean mousePositionOverlay()
	{
		return false;
	}

	@ConfigItem(
		keyName = "debugMenuAction",
		name = "Debug menu actions",
		description = "Debugs attempted menu actions to the console",
		section = interaction,
		position = 9
	)
	default boolean debugMenuActions()
	{
		return false;
	}

	@ConfigItem(
		keyName = "debugDialogs",
		name = "Debug dialog interactions",
		description = "Debugs chat dialog actions to console",
		section = interaction,
		position = 10
	)
	default boolean debugDialogs()
	{
		return false;
	}

	@ConfigItem(
		keyName = "printStackTrace",
		name = "Print Stack Trace",
		description = "Button that prints out the current stack trace",
		position = 100
	)
	default Button printStackTrace()
	{
		return new Button();
	}
}