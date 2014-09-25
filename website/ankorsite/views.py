import os
import os.path
from django.http import HttpResponse, HttpResponseNotFound
from django.template import RequestContext, loader
from django.core.files import File

SITE_ROOT = os.path.dirname(os.path.realpath(__file__))

ANKOR_STABLE_VERSION = "0.4.0"
ANKOR_LATEST_VERSION = "0.4.0"


def path_to_steps(type, step):
    return open(SITE_ROOT + '/templates/tutorial/' + type + '/' + type + '_step_' + step + '.md', 'r')


def get_num_tutorials(type):
    type_path = SITE_ROOT + '/templates/tutorial/' + type + '/'
    if os.path.isdir(type_path):
        return len(os.listdir(type_path))
    else:
        return 0


def get_titles(type):
    titles = [""] * get_num_tutorials(type)
    for x in range(0, len(titles)):
        with path_to_steps(type, str(x)) as f:
            line = f.readline()
        try:
            titles[x] = line.replace('### ', '')
        except Exception:
            print("Tutorials needs to start with '### '")
    print "TITLES ", titles
    return titles


type_names = {
    'fx': 'JavaFX Client',
    'js': 'HTML5 Client',
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
    try:
        with open(SITE_ROOT + '/templates/index_col_1.md', 'r') as f:
            mdcontent1 = File(f).read()
        with open(SITE_ROOT + '/templates/index_col_2.md', 'r') as f:
            mdcontent2 = File(f).read()
        with open(SITE_ROOT + '/templates/index_col_3.md', 'r') as f:
            mdcontent3 = File(f).read()
    except IOError:
        return HttpResponseNotFound()

    template = loader.get_template('index.html')
    context = RequestContext(request, {
        'activeMenu': 'home',
        'activeMenuText': 'Home',
        'mdcontent1': mdcontent1,
        'mdcontent2': mdcontent2,
        'mdcontent3': mdcontent3
    })

    return HttpResponse(template.render(context))


def get_bundle_md_content(name):
    with open(SITE_ROOT + '/templates/download/' + name + '.md', 'r') as f:
        return File(f).read()


def download(request):
    try:
        with open(SITE_ROOT + '/templates/download.md', 'r') as f:
            mdcontent = File(f).read()

    except IOError:
        return HttpResponseNotFound()

    mdcontent = mdcontent.replace("$VERSION", ANKOR_STABLE_VERSION)
    template = loader.get_template('download.html')
    context = RequestContext(request, {
        'activeMenu': 'download',
        'activeMenuText': 'Download',
        'stableVersion': ANKOR_STABLE_VERSION,
        'latestVersion': ANKOR_LATEST_VERSION,
        'mdcontent': mdcontent,
        'server_viewmodel': get_bundle_md_content('server_viewmodel'),
        'socket_fx_client': get_bundle_md_content('socket_fx_client'),
        'socket_server': get_bundle_md_content('socket_server'),
        'websocket_fx_client': get_bundle_md_content('websocket_fx_client'),
        'websocket_server': get_bundle_md_content('websocket_server'),
        'js_client': get_bundle_md_content('js_client')
    })

    return HttpResponse(template.render(context))


def tutorials_overview(request):
    template = loader.get_template('tutorials.html')

    context = RequestContext(request, {
        'activeMenu': 'tutorials',
        'activeMenuText': 'Tutorials',
    })

    return HttpResponse(template.render(context))


def tutorial_helper(request, type, step, template):
    try:
        with path_to_steps(type, step) as f:
            content = File(f).read()
    except IOError:
        return HttpResponseNotFound()

    step = int(step)

    next_step = step + 1
    has_next_step = True
    if next_step > num_tutorials[type] - 1:
        has_next_step = False

    prev_step = step - 1
    has_prev_step = True
    if prev_step < 0:
        has_prev_step = False

    context = RequestContext(request, {
        'activeMenu': 'tutorials',
        'activeMenuText': 'Tutorials',
        'type': type,
        'type_name': type_names[type],
        'step': step,
        'tutorial_titles': tutorial_titles[type],
        'tutorial_title': tutorial_titles[type][step],
        'hasPrevStep': has_prev_step,
        'prevStep': prev_step,
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
        'activeMenuText': 'Documentation',
        'releaseVersions': ['0.4', '0.3', '0.2'],
        'stableVersion': ANKOR_STABLE_VERSION,
        'latestVersion': ANKOR_LATEST_VERSION
    })

    return HttpResponse(template.render(context))


def manual(request, version):
    template = loader.get_template('manual/manual.html')

    try:
        with open(SITE_ROOT + '/templates/manual/manual-' + version + '.md', 'r') as f:
            mdcontent = File(f).read()
    except IOError:
        return HttpResponseNotFound()

    context = RequestContext(request, {
        'activeMenu': 'documentation',
        'activeMenuText': 'Documentation',
        'version': version,
        'mdcontent': mdcontent
    })

    return HttpResponse(template.render(context))


def spidoc(request, version):
    template = loader.get_template('spidoc/spidoc.html')

    try:
        with open(SITE_ROOT + '/templates/spidoc/spidoc-' + version + '.md', 'r') as f:
            mdcontent = File(f).read()
    except IOError:
        return HttpResponseNotFound()

    context = RequestContext(request, {
        'activeMenu': 'documentation',
        'activeMenuText': 'Documentation',
        'version': version,
        'mdcontent': mdcontent
    })

    return HttpResponse(template.render(context))


def contribute(request):
    template = loader.get_template('contribute.html')

    context = RequestContext(request, {
        'activeMenu': 'contribute',
        'activeMenuText': 'Contribute'
    })

    return HttpResponse(template.render(context))


def stateless(request):
    template = loader.get_template('spidoc/spidoc.html')

    try:
        with open(SITE_ROOT + '/templates/tutorial/stateless.md', 'r') as f:
            mdcontent = File(f).read()
    except IOError:
        return HttpResponseNotFound()

    context = RequestContext(request, {
        'activeMenu': 'documentation',
        'activeMenuText': 'Documentation',
        'mdcontent': mdcontent
    })

    return HttpResponse(template.render(context))

