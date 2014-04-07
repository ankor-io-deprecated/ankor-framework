import urllib2

num_per_type = {
	"fx": 7, 
	"server": 8,
    "ios": 4
}

print("fetching READMEs from github...")

def fetch_and_write(type, step):
	md = urllib2.urlopen("https://raw.github.com/ankor-io/ankor-todo-tutorial/" + type + "-step-" + str(step) + "/README.md").read()
	f = open(type + "/" + type + "_step_" + str(step) + ".md", "w")
	f.write(md)
	print("completed " + type + " " + str(step))

for key in num_per_type:
	for i in range(0, num_per_type[key] + 1):
		fetch_and_write(key, i)
