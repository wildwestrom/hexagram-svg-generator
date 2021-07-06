const isProduction = process.env.NODE_ENV === 'production';

module.exports = {
    plugins: [
        require('postcss-import'),
        require('tailwindcss'),
        require('autoprefixer'),
        isProduction && require('@fullhuman/postcss-purgecss')({
            content: [
                './src/app/frontend.cljs',
            ],
            defaultExtractor: content => content.match(/[A-Za-z0-9-_:/]+/g) || [],
        }),
        isProduction && require('cssnano')({
            preset: 'default',
        }),
    ],
};
