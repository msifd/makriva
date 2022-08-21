module.exports = {
  entry: './src/index.ts',
  resolve: { extensions: ['.js', '.ts'] },
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader',
      },
    ],
  },
  devtool: false,
  target: 'node',
  output: {
    filename: 'makriva.js',
    path: __dirname,
  },
};