# 🌳 Tree Data Structures Library

A simple, efficient, and user-friendly library for working with various balanced and unbalanced tree data structures, written in Kotlin.

---

## 📌 Authors
- **Vladimir Pugovkin**
- **Tatyana Gromova**
- **Rafael**

---


## 🌲 Supported Tree Structures
- ✅ **AVL Trees**
- ✅ **Red-Black (RB) Trees**
- ✅ **Binary Search Trees (BST)**

---

## 🚀 Quick Start

### Clone Repository
```bash
git clone https://github.com/spbu-coding-2024/trees-trees-team-11.git
cd trees-trees-team-11
```

### Run Examples
Execute demos for each tree type using the following commands:

**AVL Tree**
```bash
./gradlew :examples:exampleAVL:run
```

**Red-Black Tree (RB)**
```bash
./gradlew :examples:exampleRB:run
```

**Binary Search Tree (BST)**
```bash
./gradlew :examples:exampleBST:run
```

---

## 📚 Usage Guide

To use the library, instantiate objects of supported tree types as shown below:

```kotlin
val avlTree = AVLTree<YourKeyType, YourValueType>()
```
```kotlin
val rbTree = RBTree<YourKeyType, YourValueType>()
```
```kotlin
val bsTree = BSTree<YourKeyType, YourValueType>()
```

### Supported Operations
- **`height()`** — Returns the height of the tree
- **`containsKey(key)`** — Checks if the tree contains a specific key
- **`insert(key, value)`** — Inserts a key-value pair into the tree
- **`erase(key)`** — Removes a node by key
- **`clean()`** — Clears the entire tree

---

## 📄 License

This project is licensed under the [**MIT License**](LICENSE).

© 2025

