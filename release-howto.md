# Ankor Framework release - step by step

1. Are the tutorials up to date?
2. Run the 'website/ankorsite/templates/tutorial/fetch.py' script
3. Run 'mvn javadoc:aggregate' (and rename 'website/ankorsite/static/javadoc/apidocs-VERSION' folder matching release version)
4. check that there is a valid 'website/ankorsite/templates/manual/manual-VERSION.md' file
5. add the new version to 'releaseVersions' in function 'documentation' in 'website/ankorsite/views.py'
6. run 'mvn release:prepare'
7. run 'mvn release:perform'
8. deploy new website
