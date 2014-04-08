# Ankor Framework release - step by step

1. Are the tutorials up to date?
2. Run the 'website/ankorsite/templates/tutorial/fetch.py' script
3. Run 'mvn javadoc:aggregate' (and rename 'website/ankorsite/static/javadoc/apidocs-VERSION' folder matching release version)
4. check that there is a valid 'website/ankorsite/templates/manual/manual-VERSION.md' file
5. add the new version to 'releaseVersions' in function 'documentation' in 'website/ankorsite/views.py'
6. make sure that caching is enabled in 'website/ankorsite/settings.py'
7. run 'mvn release:prepare'
8. run 'mvn release:perform'
9. deploy new website
10. rename the productive tutorial repository 'ankor-tutorials' on github to 'ankor-tutorials-OLDVERSION'
11. rename the snapshot tutorial repository 'ankor-tutorials-VERSION' on github to 'ankor-tutorials'
12. create a new (uninitialized) repository 'ankor-tutorials-NEXTVERSION' on github
13. push the latest tutorials to this new 'ankor-tutorials-NEXTVERSION' github repo
