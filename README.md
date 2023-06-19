# Librarian Trade Finder
A fabric mod that helps you find the enchantments you need from a Librarian Villager.

## Demo

https://user-images.githubusercontent.com/81023792/215837935-cf4967b6-59c7-4fef-88cf-0eba47f0b69f.mp4

## Installation
Download the jar from [modrinth](https://modrinth.com/mod/librarian-trade-finder).

[![](https://github.com/Prospector/badges/blob/master/modrinth-badge-72h-padded.png?raw=true)](https://modrinth.com/mod/librarian-trade-finder)

Or clone the repository and run `./gradlew build`.
The compiled jar should be under build/libs/.

## Usage

1. Encase the villager in a 1x1 area.
2. Place down a Lectern in front of the villager.
3. (Optional) Set Keybindings (Open Config Screen [Default: `C`], Start/Stop Search [Default: `O`], Select Lectern [Default: `I`])
4. Look at the Lectern and either press keybinding to Select (Default: `I`) or execute `/tradefinder select`.
5. Put enough lecterns in your <u>off-hand</u> and an axe in your main-hand. The faster the axe can break the lectern, the faster the mod will be able to search.
6. Open Config Screen with the keybinding (Default: `C`) or execute `/tradefinder config`.
7. On the left side, select the enchantments you want to search for, by simply clicking on them. Then enter the maximum price and level you want to search for.
8. Configure the other settings on the right to your liking.
9. Either press the Button `Start Search`, exit the config screen and press the keybinding to start the search (Default: `O`) or execute `/tradefinder search`.
10. If you want to stop the search before you found the enchantment, execute `/tradefinder stop` or press the keybinding (Default: `O`).


<details>
<summary><u>Usage Demo Video</u></summary>
https://github.com/Greeenman999/LibrarianTradeFinder/assets/81023792/4be7caf6-e5a3-4ee1-9bfa-bb13b7a3aa8e
</details>

### 1.19.4 and below
<details>
<summary>click to view</summary>
### Common Steps
1. Encase the villager in a 1x1 area.
2. Place down a Lectern in front of the villager.
3. Look at the Lectern and execute `/tradefinder select`.
4. Put enough lecterns in your off-hand and an axe in your main-hand. The faster the axe can break the lectern, the faster the mod will be able to search.

#### Continuation Single Search
5. Execute `/tradefinder searchSingle <enchantment> <maxPrice>` to start searching for you preferred enchantment. As soon as the mod found it, it will stop the search and message you.
6. If you want to stop the search before you found the enchantment, execute `/tradefinder stop`.

#### Continuation List Search
5. Execute `/tradefinder config` or open the config gui using modmenu.
6. Switch to 'List Enchantments' Tab. Then select all the enchantments you want to search for.
7. Execute `/tradefinder searchList` to start searching for the list. As soon as the mod found any enchantment you selected, it will stop the search and message you.
8. If you want to stop the search before you found a enchantment, execute `/tradefinder stop`.
</details>

## Common Issues

 - The error: "You are not looking at a lectern."
   - Make sure you are looking at the lectern and not the villager.
 - The lectern doesn't get broken.
   - Some anti-cheats can prevent breaking the lectern or interacting with the villager if you are not looking at it.
 - My Lecterns are disappearing.
   - Sometimes the lecterns dropped, can get behind the lectern or somewhere else. Stop the mod, break the lectern, and try to find all the items. Then start the search again.

### If you have any further issues, create an issue on [my github](https://github.com/Greeenman999/LibrarianTradeFinder/issues) or message me on Discord `@greenman999`.
