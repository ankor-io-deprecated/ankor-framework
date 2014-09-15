({
    baseUrl: '.',
    name: 'almond.js',
    include: ['ankor/ankor'],
    out: 'ankor.js',
    optimize: "none",
    wrap: {
        startFile: 'wrap-start.frag.js',
        endFile: 'wrap-end.frag.js'
    }
})
    
