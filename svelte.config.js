import sveltePreprocess from "svelte-preprocess";
import { optimizeImports } from "carbon-preprocess-svelte";

export default {
  preprocess: [
    +   sveltePreprocess(),
    optimizeImports()
  ],
};