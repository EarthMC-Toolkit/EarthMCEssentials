> [!NOTE]
> EMCE is working again, but may not be released officially until I can guarantee everything works like it used to.\
> If you would like to use an early build in the meantime, you can join the [Toolkit Discord](https://discord.com/channels/966271635894190090/966822836096364636/1347287599772860438) and visit the `#announcements` channel where you will find the required jars.

# EarthMC Essentials
![Downloads](https://img.shields.io/github/downloads/EarthMC-Toolkit/EarthMCEssentials/total)
<a href="https://discord.gg/AVtgkcRgFs"><img src="https://img.shields.io/discord/966271635894190090?logo=discord"><a/>

A fabric mod designed for EarthMC that provides info on people, places and more.

<img align="center" width="1080" src="https://cdn.modrinth.com/data/GDrr0KgP/images/b122b9977b0c6bb345569d6174b076f588edbd2c.png">

## Features
#### Commands
  - `/nationinfo` or `/towninfo` - Displays info on the specified nation or town with extra info compared to the regular `/nation` or `/town` commands provided by Towny.
  - `/alliance <name>` - Displays info on the specified alliance.
  - `/townless inviteAll/revokeAll` - Automatically invite/revoke all townless players to/from your town!
  - `/nether <x> <z>` - Quickly convert overworld coordinates into their nether counterpart.
#### On-Screen Info
  - Townless - Show a list of online townless players and their (optional) coords.
  - Nearby - See anyone whose close to you without checking the map! Configurable options include: radius, rank and distance.
#### Config screen (F4)
  - API Intervals - Set the rate at which you want data to be updated.
  - Data - Toggle on/off, screen x/y position, text colors, radius and more.

## Installation
1. Download the [latest release](https://github.com/EarthMC-Toolkit/EarthMCEssentials/releases/latest).
2. If you haven't already, download the Fabric Loader and API via [this](https://fabricmc.net/wiki/player:tutorials:install_mcl:windows) guide.
3. Make sure you also have the correct [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) for your MC version.
4. Make sure that you have both EMCE and the Fabric API jars inside `%AppData%/Roaming/.minecraft/mods/` and launch the game!
  
## FAQ
**Why am I crashing on startup?**
>*I recommend you use the [latest](https://github.com/EarthMC-Toolkit/EarthMCEssentials/releases/latest) EMCE version, with `Fabric API 0.118.0` and `Fabric Loader 0.16.10`, delete the file `emc-essentials.json` in your config folder and reboot MC. It is also worth checking the output log since some mods may conflict with EMCE.*

**Client or Server?**
> *Everything in this mod is ran on the client-side and the only data sources are the [Official API](https://earthmc.net/docs/api), Squaremap (via 
the purpose-made [EMC-Wrapper](https://github.com/EarthMC-Toolkit/EarthMC-Wrapper) library) with news and alliances being acquired from the toolkit [API](https://emc-toolkit.vercel.app/api).*
  
**Is there a Forge version?**
>*Not right now. There may be a re-write using the Architectuary API, but this won't be any time soon.*

**Can I use OptiFine?**
>*Yes, but you will need to install [OptiFabric](https://www.curseforge.com/minecraft/mc-mods/optifabric/files) in addition.*

**How can I register an alliance?**
>*Head to our [Discord](https://discord.gg/AVtgkcRgFs) and provide your alliance's info in the appropriate channel.*

### Support
If you have a problem that the FAQ does not address, you can reach us at our [discord](https://discord.gg/AVtgkcRgFs).<br>
If you encounter a bug, report it in the #mod-bugs channel in our discord above, or [create a new issue](https://github.com/EarthMC-Toolkit/EarthMCEssentials/issues/new).
****
