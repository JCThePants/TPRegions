TPRegions for Spigot 1.8
==================

TPRegions is a portal/region teleport plugin whose original goal was to create 2 identical regions with identical structures and seamlessly teleport the player from one to the other while maintaining the players yaw, pitch and relative position.

This is allows moving the player to a different location while maintaining the illusion that they are still in the same place. Note that moving the player to a different world causes the screen to go blank briefly. This cannot be helped.

If for some reason the structures cannot be oriented in the same direction, the teleport region can have a yaw adjustment set which will rotate the player relative to the region center.

Support for portals was added to take advantage of GenericsLib's centralized region lookup and event management in order to improve performance under heavy load. The main limitation of this is that the portal region must be cuboid in shape. The portal frame opening itself does not have to have a rectangular shape. Only air blocks inside the portal region are converted to portals, but the frame must be thick enough to encompass the entire portal region to prevent portal blocks from forming outside the frame. (A frame is not required)

Any teleport region created that is only one block thick becomes a portal. If the portal is one block thick horizontally, end portal blocks are used. Otherwise nether portal blocks are used.

Currently does not support BungeeCord.

Requires GenericsLib and WorldEdit plugins.

Note: Because GenericsLib is still in development, this plugin is subject to breaking changes.




