# MapPacker [![Build Status](https://travis-ci.org/BuildStatic/MapPacker.svg?branch=master)](https://travis-ci.org/BuildStatic/MapPacker)
Easily pack Minecraft maps for distribution to BuildStatic game servers.

## Requirements
Java 8 and a Bukkit server with a map you would like to pack.

## How to Use
1. Download [MapPacker](http://ci.buildstatic.net/job/MapPacker/)
2. Put [MapPacker](http://ci.buildstatic.net/job/MapPacker/) in your plugins folder
3. Start your server
4. Get your map ready
5. Type `/pack <world name> <creator>` with `<world name>` being the world name and `<creator>` the creator(s)
6. Add any extra data you need in the `map.dat` to `plugins/MapPacker/<world name>.zip/map.dat`
7. Submit it to [BuildStatic](http://buildstatic.net)

## Building
We use Maven 3. Build using `mvn install`.
