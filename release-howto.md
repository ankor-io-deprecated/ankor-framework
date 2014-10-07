# Ankor Framework release - step by step

1. Are the tutorials up to date?
1. Run the 'website/ankorsite/templates/tutorial/fetch.py' script (make sure you are fetching from ankor-tutorials-NEXTVERSION!)
1. check that there is a valid 'website/ankorsite/templates/manual/manual-VERSION.md' file
1. add the new version to 'releaseVersions' in function 'documentation' in 'website/ankorsite/views.py'
1. update ANKOR_MAJOR_VERSION, ANKOR_STABLE_VERSION and ANKOR_LATEST_VERSION in 'website/ankorsite/views.py'
1. make sure that caching is enabled in 'website/ankorsite/settings.py'
1. run 'mvn release:prepare'
1. push the generated tag to the remote repository: 'git push origin ankor-project-VERSION'
1. run 'mvn release:perform'
1. switch to tag 'ankor-project-VERSION' and run 'mvn javadoc:aggregate'
   (check the newly generated 'website/ankorsite/static/javadoc/apidocs-VERSION' folder)
1. deploy new website


# Create new tutorial (if there where major changes)

1. rename the productive tutorial repository 'ankor-tutorials' on github to 'ankor-tutorials-OLDVERSION'
1. rename the snapshot tutorial repository 'ankor-tutorials-SNAPSHOT' on github to 'ankor-tutorials'
1. create a new (uninitialized) repository 'ankor-tutorials-SNAPSHOT' on github
1. push the latest tutorials to this new 'ankor-tutorials-SNAPSHOT' github repo


# Releasing ankor-js-bower

In addition to adding the Ankor JavaScript files as a maven dependency (modules `ankor-js` and `ankor-bundle-js-client`)
there is the possibility of adding them to a JavaScript project via `bower`.

These are the steps for releasing a new version via the bower registry.

1. Go to `ankor-js/src/main/webapp/js`.
1. Run `make clean` and then `make` (`node` and `npm` have to be installed). 
   This will generate the release files in the current directory (`ankor.js`, `ankor.min.js`, `ankor-react.js`, `ankor-react.min.js`, ...).
1. Check out the `ankor-js-bower` repository somewhere on your machine: `git clone https://github.com/ankor-io/ankor-js-bower.git`.
1. Copy the release files form step 2 into the `ankor-js-bower` repository.
1. Update the version in `bower.json`.
1. Commit the changes.
1. Tag the changes with the new version, e.g. `git tag 0.3.2`.
1. Push the master branch and the tags: `git push origin master --tags`.

The files should now be available via bower.
You can test it via `bower cache clean` and `bower install ankor-js`. 
