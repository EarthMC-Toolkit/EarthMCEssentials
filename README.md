# EarthMC Essentials 
![Downloads](https://img.shields.io/github/downloads/EarthMC-Stats/EarthMCEssentials/total) 
![Activity](https://shields.io/github/commit-activity/m/EarthMC-Stats/EarthMCEssentials)
<a href="https://discord.gg/AVtgkcRgFs">
  <img src="https://img.shields.io/discord/966271635894190090?logo=discord"><a/>
             
A fabric mod designed for EarthMC that provides info on people, places and more.<br>
  
## Features
#### Commands
  - `/nationinfo` or `/towninfo` - Displays info on the specified nation or town with extra info compared to `/n` or `/t`.
  - `/alliance <name>` - Displays info on the specified alliance.
  - `/townless inviteAll` - Quickly invite all townless players to your town!
#### On-Screen Info
  - Townless - All online townless players are shown on your screen.
  - Nearby - See anyone whose close to you without checking the dynmap! Configurable options include: radius, rank and distance.
  - News - Sometimes it's hard to keep up with the news. EMCE sends news to your action bar (or chat) as soon as it is reported.
#### Config screen (F4)
  - API Intervals - Set the rate at which you want data to be updated
  - Data - Toggle on/off, screen x/y position, text colors, radius and more.

## Installation
1. Download the [latest release](https://github.com/Warriorrrr/EarthMCEssentials/releases/latest)
2. If you haven't already, download the Fabric Loader and API via [this](https://fabricmc.net/wiki/player:tutorials:install_mcl:windows) guide.
3. Make sure you also have the correct [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) for your MC version.
4. Make sure that you have both EMCE and the Fabric API jars inside `%AppData%/Roaming/.minecraft/mods/` and launch the game!
  
## FAQ
**Why am I crashing on startup?**
>*I recommend you use the [latest](https://github.com/Warriorrrr/EarthMCEssentials/releases/latest) EMCE version, with `Fabric API 0.54` and `Fabric Loader 0.14.6`, delete the file `emc-essentials.json` in your config folder and reboot MC. It is also worth checking the output log since some mods may conflict with EMCE.*

**How is data obtained?**
>*All data is acquired from our [API](https://github.com/Owen77Stubbs/EarthMC-API) in conjuction with info from the Minecraft client.*
  
**Is there a Forge version?**
>*No, we do not plan on supporting Forge as Fabric is very lightweight and easy to work with.*

**Can I use OptiFine?**
>*Yes, but you will need to install [OptiFabric](https://www.curseforge.com/minecraft/mc-mods/optifabric/files) in addition.*

**How can I register an alliance?**
>*Head to our [discord](https://discord.gg/AVtgkcRgFs) and provide your alliance's info in the appropriate channel.*

### Support
If you have a problem that the FAQ does not address, you can reach us at our [discord](https://discord.gg/AVtgkcRgFs).<br>
If you encounter a bug, report it in the #mod-bugs channel in our discord above, or [create a new issue](https://github.com/EarthMC-Stats/EarthMCEssentials/issues/new).
