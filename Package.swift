// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "shared",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "shared",
            targets: ["shared"])
    ],
    dependencies: [],
    targets: [
        .binaryTarget(
            name: "shared",
            url: "https://maven.pkg.github.com/KevinnZou/KMMDeployPlugin/io/github/kevinnzou/kmm-spm/0.0.1/kmm-spm-0.0.1.zip",
            checksum: "355250e5c06dab84ad102f3967668d06bd68f9228384c6bb4bf01190228ee182"
        )
    ]
)