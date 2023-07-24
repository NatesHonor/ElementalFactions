# Elemental Factions

Elemental Factions is a plugin for Bukkit/Spigot servers that introduces faction-based gameplay, allowing players to create and join factions, claim territory, and engage in PvP battles.

## Features

- Faction creation: Players can create their own factions and become the faction leader.
- Faction invitation: Faction leaders can invite other players to join their faction.
- Faction acceptance: Players can accept faction invitations and become members of a faction.
- Territory claiming: Factions can claim territory by selecting chunks in the game world.
- PvP gameplay: Factions can engage in PvP battles with rival factions.
- Wilderness, Warzone, and Safezone: Pre-defined factions with special gameplay rules.
- Database storage: Factions, users, invites, and claimed chunks are stored in an H2 database.

## Commands

- `/f create <factionName>`: Creates a new faction with the specified name.
- `/f invite <playerName>`: Invites a player to join your faction.
- `/f accept <playerName>`: Accepts an invitation to join a faction.
- `/f claim`: Claims the territory of the current chunk for your faction.
- `/f unclaim`: Removes the claim of the current chunk from your faction.
- `/f map`: Displays a visual map of the claimed territory for all factions.
- `/f list`: Lists all factions on the server.
- `/f info [factionName]`: Displays information about a faction.
- `/f kick <playerName>`: Kicks a player from your faction.
- `/f disband`: Disbands your faction (only available for faction leaders).
- `/f ally <factionName>`: Sends an alliance request to another faction.
- `/f ally accept <factionName>`: Accepts an alliance request from another faction.
- `/f enemy <factionName>`: Declares another faction as an enemy.
- `/raidshop`: Opens the raid shop GUI where players can purchase raid-related items.
- `/spawnershop`: Opens the spawner shop GUI where players can purchase spawners.
- `/elixir`: Opens the elixir selection GUI where players can obtain custom elixirs.
- `/horse`: Spawns a horse with upgraded speed.

## How to Use

1. Install the Elemental Factions plugin on your Bukkit/Spigot server.
2. Start the server to generate the necessary configuration files and database.
3. Use the provided commands to create factions, invite players, claim territory, and engage in faction gameplay.
4. Players can accept faction invitations, participate in PvP battles, form alliances, declare enemies, and contribute to their faction's success.
5. Explore the features and gameplay possibilities offered by Elemental Factions.

### Adding Custom Elixirs

To add custom elixirs, follow these steps:

1. Open the `elixirs.yml` file located in the plugin's folder.
2. Under the `elixirs` section, add a new entry for your elixir using the following format:


ElixirName:
name: "Elixir Name"
effects:

    "Effect:Duration:Level"



Replace `ElixirName` with a unique identifier for your elixir, `"Elixir Name"` with the desired display name of the elixir, and `"Effect:Duration:Level"` with the desired potion effect, duration, and level. For example, `"Speed:600:3"` adds a Speed effect with a duration of 600 ticks and level 3.
3. Save the `elixirs.yml` file.
4. Restart the server or reload the plugin for the changes to take effect.
5. Players can now find the custom elixir in the elixir selection GUI (`/elixir`) and obtain it by clicking on it.

### List of effects:

- SPEED
- SLOW
- FAST_DIGGING
- SLOW_DIGGING
- INCREASE_DAMAGE
- HEAL
- HARM
- JUMP
- CONFUSION
- REGENERATION
- DAMAGE_RESISTANCE
- FIRE_RESISTANCE
- WATER_BREATHING
- INVISIBILITY
- BLINDNESS
- NIGHT_VISION
- HUNGER
- WEAKNESS
- POISON
- WITHER
- HEALTH_BOOST
- ABSORPTION
- SATURATION
- GLOWING
- LEVITATION
- LUCK
- UNLUCK
- SLOW_FALLING
- CONDUIT_POWER
- DOLPHINS_GRACE
- BAD_OMEN
- HERO_OF_THE_VILLAGE
- DARKNESS

### Spawning a Horse with Upgraded Speed

To spawn a horse with upgraded speed, use the `/horse` command. When executed, a horse with increased speed will be spawned at your location. Please note that the horse can only be spawned once every 5 minutes.

## Changelog

- Added `/f ally <factionName>` command to send an alliance request to another faction.
- Added `/f ally accept <factionName>` command to accept an alliance request from another faction.
- Added `/f enemy <factionName>` command to declare another faction as an enemy.
- Added a timer of 5 minutes before a raid horse can be spawned again.
- Updated the `show` command to display allies with their faction names and count.
- Fixed issues with ally invitation and acceptance functionality.
- General bug fixes and improvements.

## Contributing

Contributions to Elemental Factions are welcome! If you have any improvements, bug fixes, or new features to suggest, please submit a pull request.

## License

This project is licensed under the [Restrictive View-Only License](LICENSE).

