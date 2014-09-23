# Ankor Framework release - step by step

1. Are the tutorials up to date?
2. Run the 'website/ankorsite/templates/tutorial/fetch.py' script (make sure you are fetching from ankor-tutorials-NEXTVERSION!)
3. Run 'mvn javadoc:aggregate' (and rename 'website/ankorsite/static/javadoc/apidocs-VERSION' folder matching release version)
4. check that there is a valid 'website/ankorsite/templates/manual/manual-VERSION.md' file
5. add the new version to 'releaseVersions' in function 'documentation' in 'website/ankorsite/views.py'
5. update ANKOR_STABLE_VERSION and ANKOR_LATEST_VERSION in 'website/ankorsite/views.py'
6. make sure that caching is enabled in 'website/ankorsite/settings.py'
7. run 'mvn release:prepare'
8. push the generated tag to the remote repository: 'git push origin ankor-project-VERSION'
9. run 'mvn release:perform'
10. deploy new website
11. rename the productive tutorial repository 'ankor-tutorials' on github to 'ankor-tutorials-OLDVERSION'
12. rename the snapshot tutorial repository 'ankor-tutorials-VERSION' on github to 'ankor-tutorials'
13. create a new (uninitialized) repository 'ankor-tutorials-NEXTVERSION' on github
14. push the latest tutorials to this new 'ankor-tutorials-NEXTVERSION' github repo

## `ankor-js-bower` release

In addition to adding the Ankor JavaScript files as a maven dependency (modules `ankor-js` and `ankor-bundle-js-client`)
there is the possibility of adding them to a JavaScript project via `bower`.

These are the steps for releasing a new version via the bower registry.

1. Go to `ankor-js/src/main/webapp/js`.
2. Run `make clean` and then `make` (`node` and `npm` have to be installed). 
   This will generate the release files in the current directory (`ankor.js`, `ankor.min.js`, `ankor-react.js`, `ankor-react.min.js`, ...).
4. Check out the `ankor-js-bower` repository somewhere on your machine: `git clone https://github.com/ankor-io/ankor-js-bower.git`.
5. Copy the release files form step 2 into the `ankor-js-bower` repository.
6. Update the version in `bower.json`.
6. Commit the changes.
7. Tag the changes with the new version, e.g. `git tag 0.3.2`.
8. Push the master branch and the tags: `git push origin master --tags`.

The files should now be available via bower.
You can test it via `bower cache clean` and `bower install ankor-js`. 
