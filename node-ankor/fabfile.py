from fabric.api import *

@task
def test():
    print "Running mocha"
    local("./node_modules/mocha/bin/mocha test/")
    