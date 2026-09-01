# The Unhollowing

<p>The Unhollowing is a psychological horror mod for Minecraft Forge. It adds an entity that watches from the darkness, follows players who travel alone, and makes caves and familiar places feel unsafe. The mod focuses on atmosphere, strange events, and unreliable encounters instead of constant jump scares.</p>

## Minecraft Version

- Minecraft: 1.20.1
- Mod loader: Forge
- Forge version: 47.4.23

## Installation

1. Install Forge 47.4.23 for Minecraft 1.20.1 from the official Forge website.
2. Start Minecraft with the Forge profile once, then close the game.
3. Download the latest The Unhollowing `.jar` from the GitHub Releases page.
4. Place the `.jar` file in your Minecraft `mods` folder.
5. Start Minecraft using the Forge profile.

<p>For multiplayer, install the mod on the server and on every player's client. The mod is designed to coexist with other Forge mods.</p>

## Essential Compatibility

<p>Essential is optional. When Essential is installed, The Unhollowing will use its supported multiplayer environment without requiring Essential-only code or making it a required dependency. Players can use The Unhollowing with Essential, or use it by itself with Forge.</p>

## Features - Horror Entity System

### 🟥 **The Forgotten** (Main Horror Entity)

**Appearance & Behavior:**
- ✅ Tall, spectral entity that phases in and out of visibility
- ✅ Follows players who travel alone
- ✅ Distortion shader effects when phasing
- ✅ Rare corner-peeking mechanic (appears around corners, vanishes when you look back)
- ✅ Glowing white eyes visible at night
- ✅ Staring causes nausea and confusion effects

**Learning System:**
- ✅ **NEVER FORGETS** — tracks player sprint habits permanently
- ✅ Becomes faster the more you sprint (speeds up from 0.25 → 0.45)
- ✅ Adapts behavior based on learned patterns
- ✅ No decay — memories are permanent

**Atmospheric Features:**
- ✅ Sends cryptic chat messages ("You were forgotten like me...")
- ✅ Makes nearby soft blocks crumble and disappear
- ✅ Triggers scary ambient sounds (call, breathing, footsteps)
- ✅ Converts torches behind you to redstone torches (eerie effect)
- ✅ Leaves creepy written books with player names ("A Familiar Hand")
- ✅ Only appears deep underground at night
- ✅ Avoids multiplayer encounters (only hunts alone players)

### 🟥 **Watcher Entity** (Secondary Mob)

- ✅ Spawns near isolated players
- ✅ **NEVER FORGETS** — learns and remembers sprint habits permanently
- ✅ Becomes faster as you sprint more (adaptive threat level)
- ✅ No decay system — memories are permanent
- ✅ Applies darkness and weakness effects on proximity
- ✅ 3-meter tall towering presence
- ✅ Uses custom sounds and behaviors

### 🟥 **Horror Events**

- ✅ Cave ambient sounds (footsteps, breathing) at depth
- ✅ Animals become motionless and stare at players
- ✅ Nearby animals glow and become confused
- ✅ Torches behind players turn to redstone torches instead of extinguishing
- ✅ Disappearing Blackbark structures spawn and vanish
- ✅ Deeper underground triggers more frequent horror events

### 🟥 **Custom Audio (5 OGG Sounds)**

- ✅ Forgotten call (haunting vocalization)
- ✅ Forgotten hurt sound (strained cry)
- ✅ Forgotten death sound (descending groan)
- ✅ Cave footsteps (distant, unsettling)
- ✅ Cave breathing (shallow, labored)

### 🟩 **New Biomes & Materials**

- ✅ Redwood Forest biome (foggy, tall trees)
- ✅ Blackwood Forest biome (dark, twisted trees)
- ✅ Redwood Log, Leaves, and Planks
- ✅ Blackwood Log, Leaves, and Planks
- ✅ Blackbark blocks (dark, ominous)
- ✅ Very tall Redwood and Blackwood trees (12-16+ blocks high)
- ✅ Trees prevent spawning on water

## Feature Checklist

| Feature | Status |
|---------|--------|
| Forgotten Entity | ✅ Complete |
| Watcher Entity | ✅ Complete |
| Custom Sounds (5) | ✅ Complete |
| Torch Conversion | ✅ Complete |
| Staring Mobs | ✅ Complete |
| Distorted Animals | ✅ Complete |
| Chat Messages | ✅ Complete |
| Written Books | ✅ Complete |
| Corner Peeking | ✅ Complete |
| Glowing Eyes (Night) | ✅ Complete |
| Redwood/Blackwood Materials | ✅ Complete |
| World Generation | ✅ Complete |
| Biome Integration | ✅ Complete |

## Gameplay Guide

### 🧠 **The Learning System — They Never Forget**

Both The Forgotten and Watcher entities learn from your behavior:

- **Sprint Tracking** — Every time you sprint, the entities track it
- **Speed Adaptation** — As you sprint more, they become faster
- **Permanent Memory** — There is NO decay; they remember forever
- **Escalating Threat** — The more you run, the quicker they become

**Speed Progression:**
- 0-100 habits: 0.25-0.28 speed (slow, studying)
- 100-300 habits: 0.28-0.32 speed (learning)
- 300-600 habits: 0.32-0.40 speed (becoming dangerous)
- 600-1000 habits: 0.40-0.45 speed (very dangerous)
- 1000+ habits: 0.45+ speed (they've mastered your patterns)

**Strategic Advice:**
- Avoid sprinting to keep them slow
- Mix up your behavior to confuse them
- But be warned: they remember EVERYTHING you do

1. Travel deep underground (Y < 48)
2. Make sure you're alone (no other players nearby)
3. Wait at night or in dark areas
4. Stay away from torches and lights
5. Listen for ambient sounds

### What to Expect

- Rare appearances that feel organic and unreliable
- Atmospheric horror rather than constant threats
- Strange events in familiar places
- Caves and darkness feeling unsafe
- A sense of being watched

## Development Status

The complete horror atmosphere is now active. Isolated players deep underground experience:
- The Forgotten entity with phasing, corner-peeking, and distortion effects
- Watcher entity that learns player behavior
- Cave ambience with eerie sounds
- Torch conversion creating an ominous red glow
- Animals becoming still and watching
- Strange written books appearing
- Tall, twisted Redwood and Blackwood forests in applicable biomes

All features are fully functional and integrated with Minecraft's world generation, entity system, and sound engine.

<p>Redwood and Blackwood are separate wood families with their own logs, leaves, and planks. Dedicated Redwood Forest and Blackwood Forest biomes are registered for future biome placement, while current trees only place on dirt, grass, or podzol and never on water.</p>

## Horror Checklist

<p>The current build includes motionless staring mobs, torches that can go out behind the player, custom cave footsteps and breathing, temporary structures that disappear when approached, confused and glowing animals, a habit-aware Forgotten that targets isolated players, stronger events deeper underground, and books addressed to the player's own name.</p>
