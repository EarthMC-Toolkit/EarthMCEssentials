> [!WARNING]
> EMCE is currently broken due to various updates to EarthMC.
> 
> While I do plan on updating when I get time, it may be a while yet since I am very busy.\
> If you know your way around this project and/or EMCW, please consider contributing to get it working again!

# EarthMC Essentials 
![Downloads](https://img.shields.io/github/downloads/EarthMC-Toolkit/EarthMCEssentials/total)
<a href="https://discord.gg/AVtgkcRgFs">
  <img src="https://img.shields.io/discord/966271635894190090?logo=discord"><a/>
             
A fabric mod designed for EarthMC that provides info on people, places and more.

## Features
#### Commands
  - `/nationinfo` or `/towninfo` - Displays info on the specified nation or town with extra info compared to the regular `/nation` or `/town` commands provided by Towny.
  - `/alliance <name>` - Displays info on the specified alliance.
  - `/townless inviteAll/revokeAll` - Automatically invite/revoke all townless players to/from your town!
  - `/nether <x> <z>` - Quickly convert overworld coordinates into their nether counterpart.
#### On-Screen Info
  - Townless - Show a list of online townless players and their (optional) coords.
  - Nearby - See anyone whose close to you without checking the dynmap! Configurable options include: radius, rank and distance.
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
>*I recommend you use the [latest](https://github.com/EarthMC-Toolkit/EarthMCEssentials/releases/latest) EMCE version, with `Fabric API 0.81.0` and `Fabric Loader 0.14.19`, delete the file `emc-essentials.json` in your config folder and reboot MC. It is also worth checking the output log since some mods may conflict with EMCE.*

**Client or Server?**
> *Everything in this mod is ran on the client-side and the only data source is Dynmap (via 
the purpose-made [EMC-Wrapper](https://github.com/EarthMC-Toolkit/EarthMC-Wrapper) library) with custom data being acquired from the [API](https://emc-toolkit.vercel.app/api).*
  
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
