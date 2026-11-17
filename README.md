# AddurDisc (UNOFFICIAL) — Fabric 1.21.1

An unofficial Fabric port of [AddurDisc](https://modrinth.com/mod/addurdisc) for Minecraft 1.21.1. It keeps the original custom music-disc workflow and adapts dynamic jukebox songs and resource packs to Minecraft 1.21.1.

## Features

- Reads `.ogg` files from the configured AddurDisc folder and creates a music disc for each valid file.
- Generates the sound definitions, disc textures, item models, names, descriptions, jukebox-song data, and item tags.
- Supports custom resource-pack paths, optional language-file refresh, and the Creeper music-disc drop setting.
- The same custom audio files must be available on the client and server.

## Requirements

- Minecraft Java Edition 1.21.1
- Fabric Loader 0.16.9 or newer
- Fabric API
- Java 21

Install the mod on both the client and server. On first launch, AddurDisc creates its default configuration at `config/AddurDisc-common.json` and its resource folder at `.addurdisc` inside the game directory.

Put lowercase `.ogg` filenames in `.addurdisc/assets/addurdisc/sounds/`. Supported filename characters are lowercase letters, digits, periods, underscores, and hyphens. Restart the game after adding or removing audio files. The generated discs appear in the AddurDisc creative tab.

`canDropByCreeper` controls whether the generated discs join Minecraft's Creeper music-disc drop tag. `refreshLang` controls whether the generated English names are rewritten at startup. `customPath` and `customPathName` select a different resource folder.

## Port and license

Original project by TuYw: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/addurdisc) · [Modrinth](https://modrinth.com/mod/addurdisc) · [source](https://github.com/TunYuntuwuQWQ/addurdisc).

The upstream source is licensed under the MIT License. The original `LICENSE` file and project icon are retained. This port is not affiliated with or endorsed by the original author.

AI assistance was used to adapt the Fabric port to Minecraft 1.21.1 and review its build configuration.
