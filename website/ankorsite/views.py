import os
import os.path
from django.http import HttpResponse, HttpResponseNotFound
from django.template import RequestContext, loader
from django.core.files import File

SITE_ROOT = os.path.dirname(os.path.realpath(__file__))


def path_to_steps(type, step):
    return open(SITE_ROOT + '/templates/tutorial/' + type + '/' + type + '_step_' + step + '.md', 'r')


def get_num_tutorials(type):
    path, dirs, files = os.walk(SITE_ROOT + '/templates/tutorial/' + type + '/').next()
    return len(files)


def get_titles(type):
    titles = [""] * get_num_tutorials(type)
    for x in range(0, len(titles)):
        path = path_to_steps(type, str(x))
        line = path.readline()
        try:
            titles[x] = line.replace('### ', '')
        except Exception:
            print("Tutorials needs to start with '### '")
    return titles


type_names = {
    'fx': 'JavaFX Client',
    'js': 'JavaScript Client',
    'ios': 'iOS Client',
    'server': 'Ankor Server'
}

num_tutorials = {
    'fx': get_num_tutorials('fx'),
    'js': get_num_tutorials('js'),
    'ios': get_num_tutorials('ios'),
    'server': get_num_tutorials('server')
}

tutorial_titles = {
    'fx': get_titles('fx'),
    'js': get_titles('js'),
    'ios': get_titles('ios'),
    'server': get_titles('server')
}


def index(request):
    template = loader.get_template('index.html')
    context = RequestContext(request, {
        'activeMenu': 'home',
    })
    return HttpResponse(template.render(context))


def download(request):
    template = loader.get_template('download.html')
    context = RequestContext(request, {
        'activeMenu': 'download',
    })
    return HttpResponse(template.render(context))


def tutorials_overview(request):
    template = loader.get_template('tutorials.html')
    context = RequestContext(request, {
        'activeMenu': 'tutorials',
    })
    return HttpResponse(template.render(context))


def tutorial_helper(request, type, step, template):
    try:
        path = path_to_steps(type, step)
    except IOError:
        return HttpResponseNotFound()

    f = File(path)
    content = f.read()

    step = int(step)

    next_step = step + 1
    has_next_step = True
    if next_step > num_tutorials[type] - 1:
        has_next_step = False

    context = RequestContext(request, {
        'activeMenu': 'tutorials',
        'type': type,
        'type_name': type_names[type],
        'step': step,
        'tutorial_titles': tutorial_titles[type],
        'tutorial_title': tutorial_titles[type][step],
        'hasNextStep': has_next_step,
        'nextStep': next_step,
        'content': content
    })
    return HttpResponse(template.render(context))


def tutorials_detail(request, type):
    return tutorials(request, type, '0')


def tutorials(request, type, step):
    template = loader.get_template('tutorial/tutorial.html')
    return tutorial_helper(request, type, step, template)


def documentation(request):
    template = loader.get_template('documentation.html')
    context = RequestContext(request, {
        'activeMenu': 'documentation',
    })
    return HttpResponse(template.render(context))


def contribute(request):
    template = loader.get_template('contribute.html')
    context = RequestContext(request, {
        'activeMenu': 'contribute',
    })
    return HttpResponse(template.render(context))
