import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

export const ComponentRegistry = {
    button: {
        component: Button,
        defaultProps: {
            children: 'Click me',
            variant: 'default',
            className: 'w-full h-full text-sm'
        },
        defaultSize: {
            width: 120,
            height: 40
        },
        canHaveChildren: false,
        displayName: 'Button'
    },

    input: {
        component: Input,
        defaultProps: {
            placeholder: 'Enter text here...',
            type: 'text',
            className:'w-full h-full',

        },
        defaultSize: {
            width: 220,
            height: 30
        },
        canHaveChildren: false,
        displayName: 'Input'
    },

    card: {
        component: Card,
        defaultProps: {},
        defaultSize: {
            width: 300,
            height: 200
        },
        canHaveChildren: true,
        displayName: 'Card'
    },

    label: {
        component: Label,
        defaultProps:{
            children: 'Label',
        },
        defaultSize: {
            width: 100,
            height: 30
        },
        displayName: 'Label'
    }
};