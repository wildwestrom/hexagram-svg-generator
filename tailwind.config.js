module.exports = {
    purge: false,
    darkMode: 'media', // or 'media' or 'class'
    theme: {
        screens: {
            'xs': {'min': '410px'},
            'sm': {'min': '640px'},
            'md': {'min': '768px'},
            'lg': {'min': '1024px'},
            'xl': {'min': '1280px'},
            '2xl':{'min': '1536px'},
        }
    },
    variants: {
        extend: {
            textColor: ['visited'],
        }
    },
    plugins: [
        require('@tailwindcss/line-clamp'),
        require('@tailwindcss/typography'),
    ]
}
