package net.runelite.client.plugins.openrl.api.rs2.camera;

import lombok.extern.slf4j.Slf4j;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.ScriptID;
import net.runelite.api.TileObject;
import net.runelite.api.VarClientInt;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.camera.CameraPlugin;
import net.runelite.client.plugins.openrl.Static;
import net.runelite.client.plugins.openrl.api.commons.Time;
import net.runelite.client.plugins.openrl.api.game.Game;
import net.runelite.client.plugins.openrl.api.input.Keyboard;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2NPC;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2NPCs;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Player;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2Players;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileItem;
import net.runelite.client.plugins.openrl.api.rs2.entities.RS2TileObject;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widget;
import net.runelite.client.plugins.openrl.api.rs2.widgets.RS2Widgets;

/**
 * Source: <>https://github.com/chsami/Microbot/tree/development/runelite-client/src/main/java/net/runelite/client/plugins/microbot/util/camera</>
 */
@Slf4j
public class RS2Camera
{
	private static final NpcTracker NPC_TRACKER = new NpcTracker();

	public static int angleToTile(Actor t)
	{
		int angle = (int) Math.toDegrees(Math.atan2(t.getWorldLocation().getY() - Static.getClient().getLocalPlayer().getWorldLocation().getY(),
			t.getWorldLocation().getX() - Static.getClient().getLocalPlayer().getWorldLocation().getX()));
		return angle >= 0 ? angle : 360 + angle;
	}

	public static int angleToTile(TileObject t)
	{
		int angle = (int) Math.toDegrees(Math.atan2(t.getWorldLocation().getY() - Static.getClient().getLocalPlayer().getWorldLocation().getY(),
			t.getWorldLocation().getX() - Static.getClient().getLocalPlayer().getWorldLocation().getX()));
		return angle >= 0 ? angle : 360 + angle;
	}

	public static int angleToTile(LocalPoint localPoint)
	{
		int angle = (int) Math.toDegrees(Math.atan2(localPoint.getY() - Static.getClient().getLocalPlayer().getLocalLocation().getY(),
			localPoint.getX() - Static.getClient().getLocalPlayer().getLocalLocation().getX()));
		return angle >= 0 ? angle : 360 + angle;
	}

	public static int angleToTile(WorldPoint worldPoint)
	{
		int angle = (int) Math.toDegrees(Math.atan2(worldPoint.getY() - RS2Players.getLocal().getWorldLocation().getY(),
			worldPoint.getX() - RS2Players.getLocal().getWorldLocation().getX()));
		return angle >= 0 ? angle : 360 + angle;
	}

	public static void turnTo(final Actor actor)
	{
		int angle = getCharacterAngle(actor);
		setAngle(angle, 40);
	}

	public static void turnTo(final Actor actor, int maxAngle)
	{
		int angle = getCharacterAngle(actor);
		setAngle(angle, maxAngle);
	}

	public static void turnTo(final TileObject tileObject)
	{
		int angle = getObjectAngle(tileObject);
		setAngle(angle, 40);
	}

	public static void turnTo(final TileObject tileObject, int maxAngle)
	{
		int angle = getObjectAngle(tileObject);
		setAngle(angle, maxAngle);
	}

	public static void turnTo(final LocalPoint localPoint)
	{
		int angle = (angleToTile(localPoint) - 90) % 360;
		setAngle(angle, 40);
	}

	public static void turnTo(final LocalPoint localPoint, int maxAngle)
	{
		int angle = (angleToTile(localPoint) - 90) % 360;
		setAngle(angle, maxAngle);
	}

	public static int getCharacterAngle(Actor actor)
	{
		return getTileAngle(actor);
	}

	public static int getObjectAngle(TileObject tileObject)
	{
		return getTileAngle(tileObject);
	}

	public static int getTileAngle(Actor actor)
	{
		int a = (angleToTile(actor) - 90) % 360;
		return a < 0 ? a + 360 : a;
	}

	public static int getTileAngle(TileObject tileObject)
	{
		int a = (angleToTile(tileObject) - 90) % 360;
		return a < 0 ? a + 360 : a;
	}

	/**
	 * <h1> Checks if the angle to the target is within the desired max angle </h1>
	 * <p>
	 * The desired max angle should not go over 80-90 degrees as the target will be out of view
	 *
	 * @param targetAngle     the angle to the target
	 * @param desiredMaxAngle the maximum angle to the target (Should be a positive number)
	 * @return true if the angle to the target is within the desired max angle
	 */
	public static boolean isAngleGood(int targetAngle, int desiredMaxAngle)
	{
		return Math.abs(getAngleTo(targetAngle)) <= desiredMaxAngle;
	}

	public static void setAngle(int targetDegrees, int maxAngle)
	{
		// Default camera speed is 1
		double defaultCameraSpeed = 1f;

		// If the camera plugin is enabled, get the camera speed from the config in case it has been changed
		final Plugin cameraPlugin = Static.getPluginManager().getPlugins().stream().filter(p -> p.getClass().getName().equals(CameraPlugin.class.getName())).findFirst().get();
		if (Static.getPluginManager().isPluginEnabled(cameraPlugin))
		{
			String configGroup = "zoom";
			String configKey = "cameraSpeed";
			defaultCameraSpeed = Static.getInjector().getInstance(ConfigManager.class).getConfiguration(configGroup, configKey, double.class);
		}
		// Set the camera speed to 3 to make the camera move faster
		Static.getClient().setCameraSpeed(3f);

		if (getAngleTo(targetDegrees) > maxAngle)
		{
			Keyboard.pressed(KeyEvent.VK_LEFT);
			Time.sleepUntil(() -> Math.abs(getAngleTo(targetDegrees)) <= maxAngle, 50, 5000);
			Keyboard.released(KeyEvent.VK_LEFT);
		}
		else if (getAngleTo(targetDegrees) < -maxAngle)
		{
			Keyboard.pressed(KeyEvent.VK_RIGHT);
			Time.sleepUntil(() -> Math.abs(getAngleTo(targetDegrees)) <= maxAngle, 50, 5000);
			Keyboard.released(KeyEvent.VK_RIGHT);
		}
		Static.getClient().setCameraSpeed((float) defaultCameraSpeed);
	}

	public static void adjustPitch(float percentage)
	{
		float currentPitchPercentage = cameraPitchPercentage();

		if (currentPitchPercentage < percentage)
		{
			Keyboard.pressed(KeyEvent.VK_UP);
			Time.sleepUntil(() -> cameraPitchPercentage() >= percentage, 50, 5000);
			Keyboard.released(KeyEvent.VK_UP);
		}
		else
		{
			Keyboard.pressed(KeyEvent.VK_DOWN);
			Time.sleepUntil(() -> cameraPitchPercentage() <= percentage, 50, 5000);
			Keyboard.released(KeyEvent.VK_DOWN);
		}
	}

	public static int getPitch()
	{
		return Static.getClient().getCameraPitch();
	}

	// set camera pitch
	public static void setPitch(int pitch)
	{
		int minPitch = 128;
		int maxPitch = 383;
		// clamp pitch to avoid out of bounds
		pitch = Math.max(minPitch, Math.min(maxPitch, pitch));
		Static.getClient().setCameraPitchTarget(pitch);
	}

	public static float cameraPitchPercentage()
	{
		int minPitch = 128;
		int maxPitch = 383;
		int currentPitch = Static.getClient().getCameraPitch();

		int adjustedPitch = currentPitch - minPitch;
		int adjustedMaxPitch = maxPitch - minPitch;

		return (float) adjustedPitch / (float) adjustedMaxPitch;
	}

	public static int getAngleTo(int degrees)
	{
		int ca = getAngle();
		if (ca < degrees)
		{
			ca += 360;
		}
		int da = ca - degrees;
		if (da > 180)
		{
			da -= 360;
		}
		return da;
	}

	public static int getAngle()
	{
		// the client uses fixed point radians 0 - 2^14
		// degrees = yaw * 360 / 2^14 = yaw / 45.5111...
		// This leaves it on a scale of 45 versus a scale of 360 so we multiply it by 8 to fix that.
		return (int) Math.abs(Static.getClient().getCameraYaw() / 45.51 * 8);
	}

	/**
	 * Calculates the CameraYaw based on the given NPC or object angle.
	 *
	 * @param npcAngle the angle of the NPC or object relative to the player (0-359 degrees)
	 * @return the calculated CameraYaw (0-2047)
	 */
	public static int calculateCameraYaw(int npcAngle)
	{
		// Convert the NPC angle to CameraYaw using the derived formula
		return (1536 + (int) Math.round(npcAngle * (2048.0 / 360.0))) % 2048;
	}

	/**
	 * Track the NPC with the camera
	 *
	 * @param npcId the ID of the NPC to track
	 */
	public static void trackNpc(int npcId)
	{
		NPC_TRACKER.startTracking(npcId);
	}

	/**
	 * Stop tracking the NPC with the camera
	 */
	public static void stopTrackingNpc()
	{
		NPC_TRACKER.stopTracking();
	}

	/**
	 * Checks if a NPC is being tracked
	 *
	 * @return true if a NPC is being tracked, false otherwise
	 */
	public static boolean isTrackingNpc()
	{
		return NPC_TRACKER.isTracking();
	}

	public static boolean isTileOnScreen(TileObject tileObject)
	{
		int viewportHeight = Static.getClient().getViewportHeight();
		int viewportWidth = Static.getClient().getViewportWidth();

		Polygon poly = Perspective.getCanvasTilePoly(Static.getClient(), tileObject.getLocalLocation());

		if (poly == null) return false;

		return poly.getBounds2D().getX() <= viewportWidth && poly.getBounds2D().getY() <= viewportHeight;
	}

	public static boolean isTileOnScreen(LocalPoint localPoint)
	{
		Client client = Static.getClient();
		int viewportHeight = client.getViewportHeight();
		int viewportWidth = client.getViewportWidth();

		Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
		if (poly == null) return false;

		// Check if any part of the polygon intersects with the screen bounds
		Rectangle viewportBounds = new Rectangle(0, 0, viewportWidth, viewportHeight);
		if (!poly.intersects(viewportBounds)) return false;

		// Optionally, check if the tile is in front of the camera
		Point canvasPoint = Perspective.localToCanvas(client, localPoint, client.getPlane());
		return canvasPoint != null;
	}

	// get the camera zoom
	public static int getZoom()
	{
		return Static.getClient().getVarcIntValue(VarClientInt.CAMERA_ZOOM_RESIZABLE_VIEWPORT);
	}

	public static void setZoom(int zoom)
	{
		Static.getClientThread().invokeLater(() ->
		{
			Static.getClient().runScript(ScriptID.CAMERA_DO_ZOOM, zoom, zoom);
		});
	}

	// Get camera/compass facing
	public static int getYaw()
	{
		return Static.getClient().getCameraYaw();
	}

	// Set camera/compass facing
	// North = 0, 2048
	// East = 1536
	// South = 1024
	// West = 512

	public static void setYaw(int yaw)
	{
		if (yaw >= 0 && yaw < 2048)
		{
			Static.getClient().setCameraYawTarget(yaw);
		}
	}

	/**
	 * Resets the camera pitch to 280 if it is currently less than 280.
	 */
	public static void resetPitch()
	{
		// Set the camera pitch to 280
		if (getPitch() < 280)
			setPitch(280);
	}

	/**
	 * Resets the camera zoom to 200 if it is currently greater than 200.
	 */
	public static void resetZoom()
	{
		// Set the camera zoom to 200
		if (getZoom() > 200)
			setZoom(200);
	}

	/**
	 * Determines whether the specified tile is centered on the screen within a given tolerance.
	 * <p>
	 * Projects the tile to screen space, computes its bounding rectangle, and then checks
	 * whether that rectangle lies entirely inside a centered “box” whose width and height
	 * are the given percentage of the viewport dimensions.
	 * </p>
	 *
	 * @param tile             the local tile coordinate to test (may not be null)
	 * @param marginPercentage the size of the centered tolerance box, expressed as a percentage
	 *                         of the viewport (e.g. 10.0 for 10%)
	 * @return {@code true} if the tile’s screen bounds lie entirely within the centered margin box;
	 * {@code false} if the tile cannot be projected or lies outside that box
	 */
	public static boolean isTileCenteredOnScreen(LocalPoint tile, double marginPercentage)
	{
		Polygon poly = Perspective.getCanvasTilePoly(Static.getClient(), tile);
		if (poly == null) return false;

		Rectangle tileBounds = poly.getBounds();
		int viewportWidth = Static.getClient().getViewportWidth();
		int viewportHeight = Static.getClient().getViewportHeight();
		int centerX = viewportWidth / 2;
		int centerY = viewportHeight / 2;

		int marginX = (int) (viewportWidth * (marginPercentage / 100.0));
		int marginY = (int) (viewportHeight * (marginPercentage / 100.0));

		Rectangle centerBox = new Rectangle(
			centerX - marginX / 2,
			centerY - marginY / 2,
			marginX,
			marginY
		);

		return centerBox.contains(tileBounds);
	}

	/**
	 * Determines whether the specified tile is centered on the screen, using a default
	 * margin tolerance of 10%.
	 *
	 * @param tile the local tile coordinate to test (may not be null)
	 * @return {@code true} if the tile’s screen bounds lie entirely within the centered
	 * 10% margin box; {@code false} otherwise
	 * @see #isTileCenteredOnScreen(LocalPoint, double)
	 */
	public static boolean isTileCenteredOnScreen(LocalPoint tile)
	{
		return isTileCenteredOnScreen(tile, 10);
	}

	/**
	 * Rotates the camera to center on the specified tile, if it is not already within
	 * the given margin tolerance.
	 * <p>
	 * Computes the bearing from the camera to the tile, adjusts it into a [0–360) range,
	 * and then issues a small-angle camera turn if {@link #isTileCenteredOnScreen(LocalPoint, double)}
	 * returns {@code false}.
	 * </p>
	 *
	 * @param tile             the local tile coordinate to center on (may not be null)
	 * @param marginPercentage the size of the centered tolerance box, expressed as a percentage
	 *                         of the viewport (e.g. 10.0 for 10%)
	 * @see #angleToTile(LocalPoint)
	 * @see #setAngle(int, int)
	 */
	public static void centerTileOnScreen(LocalPoint tile, double marginPercentage)
	{
		// Calculate the desired camera angle for the tile
		int rawAngle = angleToTile(tile) - 90;
		int angle = rawAngle < 0 ? rawAngle + 360 : rawAngle;
		// Center if not already within margin
		if (!isTileCenteredOnScreen(tile, marginPercentage))
		{
			setAngle(angle, 5); // Use small max angle for precision
		}
	}

	/**
	 * Rotates the camera to center on the specified tile, using a default
	 * margin tolerance of 10%.
	 *
	 * @param tile the local tile coordinate to center on (may not be null)
	 * @see #centerTileOnScreen(LocalPoint, double)
	 */
	public static void centerTileOnScreen(LocalPoint tile)
	{
		centerTileOnScreen(tile, 10.0);
	}

	@Slf4j
	public static class NpcTracker
	{
		private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Single-threaded scheduler
		private ScheduledFuture<?> trackingTask; // Future to manage the scheduled task

		// The original method to track the actor
		private static void trackNpc(int npcId)
		{
			if (!Game.isLoggedIn())
			{
				return;
			}
			Actor actor = RS2NPCs.getNearest(npcId).getNpc(); // Get the actor
			if (actor == null)
			{
				return; // Actor not found, do nothing
			}
			Static.getClient().setCameraYawTarget(RS2Camera.calculateCameraYaw(RS2Camera.angleToTile(actor)));
		}

		/**
		 * Method to start tracking the NPC
		 *
		 * @param npcId the ID of the NPC to track
		 */
		public void startTracking(int npcId)
		{
			if (trackingTask != null && !trackingTask.isCancelled())
			{
				log.info("Already tracking an NPC");
				return; // Already tracking, do nothing
			}

			// Schedule the trackActor method to run every 50 milliseconds
			trackingTask = scheduler.scheduleAtFixedRate(() -> trackNpc(npcId), 0, 200, TimeUnit.MILLISECONDS);
			log.info("Started tracking NPC with ID: {}", npcId);
		}

		/**
		 * Method to stop tracking the NPC
		 */
		public void stopTracking()
		{
			if (trackingTask != null)
			{
				trackingTask.cancel(true); // Cancel the scheduled task
				trackingTask = null;
				log.info("Stopped tracking NPC");
			}
		}

		/**
		 * Method to check if a NPC is being tracked
		 *
		 * @return true if a NPC is being tracked, false otherwise
		 */
		public boolean isTracking()
		{
			return trackingTask != null;
		}
	}

	public static Rectangle getClickableViewport()
	{
		final RS2Widget fixedLayout = RS2Widgets.getWidget(InterfaceID.Toplevel.MAINMODAL);
		final RS2Widget classicLayout = RS2Widgets.getWidget(InterfaceID.ToplevelOsrsStretch.MAINMODAL);
		final RS2Widget modernLayout = RS2Widgets.getWidget(InterfaceID.ToplevelPreEoc.MAINMODAL);

		final Rectangle clickableViewport = fixedLayout != null ? fixedLayout.getBounds() : classicLayout != null ? classicLayout.getBounds() : modernLayout != null ? modernLayout.getBounds() : null;
		return clickableViewport;
	}

	public static boolean isInClickableViewport(Object sceneEntity)
	{
		if (sceneEntity == null)
		{
			return false;
		}

		final Rectangle clickableViewport = getClickableViewport();
		if (clickableViewport == null)
		{
			return false;
		}

		if (sceneEntity instanceof RS2NPC && clickableViewport.intersects(((RS2NPC) sceneEntity).getNpc().getCanvasTilePoly().getBounds()))
		{
			return true;
		}
		else if (sceneEntity instanceof RS2Player && clickableViewport.intersects(((RS2Player) sceneEntity).getCanvasTilePoly().getBounds()))
		{
			return true;
		}
		else if (sceneEntity instanceof RS2TileObject && clickableViewport.intersects(((RS2TileObject) sceneEntity).getCanvasTilePoly().getBounds()))
		{
			return true;
		}
		else if (sceneEntity instanceof RS2TileItem && clickableViewport.intersects(Perspective.getCanvasTilePoly(Static.getClient(), ((RS2TileItem) sceneEntity).getLocalLocation()).getBounds()))
		{
			return true;
		}
		return false;
	}

	public static void turnToSceneEntityIfOutsideClickableViewport(Object sceneEntity)
	{
		if (sceneEntity == null || isInClickableViewport(sceneEntity))
		{
			return;
		}

		if (sceneEntity instanceof RS2NPC)
		{
			turnTo(((RS2NPC) sceneEntity).getNpc());
		}
		else if (sceneEntity instanceof RS2Player)
		{
			turnTo(((RS2Player) sceneEntity).getPlayer());
		}
		else if (sceneEntity instanceof RS2TileObject)
		{
			turnTo(((RS2TileObject) sceneEntity).getTileObject());
		}
		else if (sceneEntity instanceof RS2TileItem)
		{
			turnTo(((RS2TileItem) sceneEntity).getLocalLocation());
		}
	}
}