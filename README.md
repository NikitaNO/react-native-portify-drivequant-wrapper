
# react-native-portify-drivequant-wrapper

## Getting started

`$ npm install react-native-portify-drivequant-wrapper --save`

### Mostly automatic installation

`$ react-native link react-native-portify-drivequant-wrapper`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-portify-drivequant-wrapper` and add `RNPortifyDrivequantWrapper.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNPortifyDrivequantWrapper.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNPortifyDrivequantWrapperPackage;` to the imports at the top of the file
  - Add `new RNPortifyDrivequantWrapperPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-portify-drivequant-wrapper'
  	project(':react-native-portify-drivequant-wrapper').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-portify-drivequant-wrapper/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-portify-drivequant-wrapper')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNPortifyDrivequantWrapper.sln` in `node_modules/react-native-portify-drivequant-wrapper/windows/RNPortifyDrivequantWrapper.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Portify.Drivequant.Wrapper.RNPortifyDrivequantWrapper;` to the usings at the top of the file
  - Add `new RNPortifyDrivequantWrapperPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNPortifyDrivequantWrapper from 'react-native-portify-drivequant-wrapper';

// TODO: What to do with the module?
RNPortifyDrivequantWrapper;
```
  