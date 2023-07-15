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

## How to Use

1. Install the Elemental Factions plugin on your Bukkit/Spigot server.
2. Start the server to generate the necessary configuration files and database.
3. Use the provided commands to create factions, invite players, claim territory, and engage in faction gameplay.
4. Players can accept faction invitations, participate in PvP battles, and contribute to their faction's success.
5. Explore the features and gameplay possibilities offered by Elemental Factions.

## Contributing

Contributions to Elemental Factions are welcome! If you have any improvements, bug fixes, or new features to suggest, please submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).
