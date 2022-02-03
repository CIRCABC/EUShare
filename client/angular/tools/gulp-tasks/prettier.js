const srcsToFmt = [
  "src/**/*.{js,ts}",
  "!src/app/openapi/**/*.ts",
  "!src/app/app-routing.module.ts",
];

const csssToFmt = ["src/**/*.css"];

module.exports = {
  format: (gulp) => () => {
    const prettier = require("gulp-prettier");
    return gulp
      .src(srcsToFmt, { base: "." })
      .pipe(prettier({ singleQuote: true }))
      .pipe(gulp.dest("."));
  },
  css: (gulp) => () => {
    const prettier = require("gulp-prettier");
    return gulp
      .src(csssToFmt, { base: "." })
      .pipe(prettier({ singleQuote: true }))
      .pipe(gulp.dest("."));
  },
};
