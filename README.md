TextureSuite
---
TextureSuite is a very simple command-based texture pack manager for Bukkit servers. You can use it to present a list of recommended texture packs to your users.

by **krinsdeath**

Want to Contribute?
---
TextureSuite uses maven to handle its dependencies and build cycle.

    git clone https://github.com/krinsdeath/texturesuite.git texturesuite
    cd texturesuite
    mvn -U clean package

Commands
---
*   To print help about TextureSuite: */texture* or */texture ?*
*   To show a list of available packs: */texture list*
*   To get a specific pack: */texture get [pack]*
*   To add a pack to the list: */texture add [pack] [url]*
*   To remove a pack: */texture remove [pack]*
*   To reload TextureSuite's config: */texture reload*
