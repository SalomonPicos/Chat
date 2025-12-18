# Chat
This addon gives players Island Chat for any BentoBox gamemode (internamente continua a usare il team dell'isola).

## Island chat

Quando attiva, la chat va solo ai membri del team dell'isola. I giocatori possono attivare/disattivare il canale island chat, gli admin possono spiare tutte le island chat.

## Commands
### Player commands

* `chat` - toggles whether player's chat goes to the island channel or not

### Admin commands

* `chatspy` - toggles whether player's chat goes to the island channel or not

The config also has settings to log all chats if required.

## Configuration

```
# Configuration file for Chat
island-chat:
  gamemodes:
  - BSkyBlock
  - AcidIsland
  - CaveBlock
  - SkyGrid
  # Log island chats to console.
  log: false
```

## Permissions

```
permissions:
  bskyblock.chat.island-chat:
    description: Player can use island chat
    default: true
  bskyblock.chat.spy:
    description: Player can use island chat spy
    default: op
 
  acidisland.chat.island-chat:
    description: Player can use island chat
    default: true
  acidisland.chat.spy:
    description: Player can use island chat spy
    default: op

  caveblock.chat.island-chat:
    description: Player can use island chat
    default: true
  caveblock.chat.spy:
    description: Player can use island chat spy
    default: op

  skygrid.chat.island-chat:
    description: Player can use island chat
    default: true
  skygrid.chat.spy:
    description: Player can use island chat spy
    default: op
 
```

## Like this addon?
You can [sponsor](https://github.com/sponsors/tastybento) to get more addons like this and make this one better!
