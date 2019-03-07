// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.
export const environment = {
  production: false,
  amplify: {
    Auth: {
      identityPoolId: 'us-east-1:c5d7a840-4f84-4238-ab60-662ac61e72e4',
      region: 'us-east-1',
      userPoolId: 'us-east-1_rzOdKNdcv',
      userPoolWebClientId: '6her35acoifrnmia95q8sjvovl'
    }
  },
  categoriesUrl : 'https://8u4e5ji3bd.execute-api.us-east-1.amazonaws.com/dev/course-category'
};