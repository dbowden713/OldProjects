# RO Item Checker
A small program I wrote to help a developer on a small private server for the MMORPG, Ragnarok Online. Parses a flatfile database of items in the game and returns a list of items with a specific script type (OnUnequip_Script).

### Installation
This is a standalone program. Just run!

### Usage
The program has a simple GUI to load the database and display results.
- Database format:  
`ID,AegisName,Name,Type,Buy,Sell,Weight,ATK[:MATK],DEF,Range,Slots,Job,Class,Gender,Loc,wLV,eLV[:maxLevel],Refineable,View,{ Script },{ OnEquip_Script },{ OnUnequip_Script }`
- Example Item:  
`5385,Yoyo_Hat,Yoyo Hat,4,20,,300,,2,,0,0xFFFFFFFF,63,2,256,,20,1,391,{ skill "TF_HIDING",1; },{},{ sc_end SC_HIDING; }`  
This item has an OnUnequip_Script - `sc_end SC_HIDING;` - so it will be returned by the program.

**[Sample Database Here](https://raw.githubusercontent.com/rathena/rathena/master/db/re/item_db.txt)**

### Credits
[dbowden713](https://github.com/dbowden713)
